package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.dto.partialcredit.BuyerCodeOption;
import com.ecoatm.salesplatform.dto.partialcredit.BuyerUserOption;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Sales-rep on-behalf submission for partial-credit requests
 * (Sprint 4 chunk 6 — SPKB-3659). Three operations:
 *
 * <ol>
 *   <li>List active buyer codes the rep can file on behalf of.</li>
 *   <li>List the ecoATM Direct users associated with the buyer companies
 *       on a given buyer code (so the rep can pick whose name to stamp
 *       on the draft).</li>
 *   <li>Create a draft + stamp the on-behalf columns in one transaction.</li>
 * </ol>
 *
 * <p><b>Scoping caveat (Phase 1):</b> V89 has no direct mapping between
 * an authenticated user and a {@code sales_representatives} row — the
 * plan's "rep.buyer_sales_reps junction must include the code" is not
 * implementable without a {@code sales_representatives.user_id} column.
 * This service therefore lets any user with the {@code SalesRep} role
 * file for any active buyer code. The role gate at the controller is
 * sufficient until Phase 2 adds the user→rep mapping.
 *
 * <p>Eligibility-filter SQL is parked here so the future-Phase-2 change
 * is a single-method swap: replace the predicate inside
 * {@link #listEligibleBuyerCodes} and add a WHERE on
 * {@code buyer_sales_reps.sales_rep_id}.
 */
@Service
public class OnBehalfSubmissionService {

    private final JdbcTemplate jdbc;
    private final CreditRequestService creditRequestService;
    private final CreditRequestRepository creditRequestRepository;

    public OnBehalfSubmissionService(
            JdbcTemplate jdbc,
            CreditRequestService creditRequestService,
            CreditRequestRepository creditRequestRepository) {
        this.jdbc = jdbc;
        this.creditRequestService = creditRequestService;
        this.creditRequestRepository = creditRequestRepository;
    }

    /** Active buyer codes the rep can pick from. Phase-1 permissive
     *  ({@code WHERE bc.status='Active' AND bc.soft_delete=FALSE});
     *  Phase 2 will tighten via {@code buyer_sales_reps}. */
    @Transactional(readOnly = true)
    public List<BuyerCodeOption> listEligibleBuyerCodes(Long salesRepUserId) {
        // salesRepUserId is unused today — present so the Phase-2 scope
        // tightening is a no-op signature change.
        return jdbc.query(
                """
                SELECT bc.id,
                       bc.code,
                       MIN(b.company_name) AS buyer_name
                FROM   buyer_mgmt.buyer_codes bc
                JOIN   buyer_mgmt.buyer_code_buyers bcb ON bcb.buyer_code_id = bc.id
                JOIN   buyer_mgmt.buyers b              ON bcb.buyer_id      = b.id
                WHERE  bc.status      = 'Active'
                  AND  bc.soft_delete = FALSE
                  AND  b.status       = 'Active'
                GROUP BY bc.id, bc.code
                ORDER BY bc.code
                """,
                (rs, rowNum) -> new BuyerCodeOption(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("buyer_name")));
    }

    /** Buyer users (ecoATM Direct users) associated with at least one
     *  buyer company on the supplied code. The rep picks one of these to
     *  stamp on the draft so the audit trail records who the request is
     *  actually for. */
    @Transactional(readOnly = true)
    public List<BuyerUserOption> listBuyersForCode(Long salesRepUserId, Long buyerCodeId) {
        return jdbc.query(
                """
                SELECT DISTINCT
                       u.id        AS user_id,
                       a.full_name AS full_name,
                       a.email     AS email
                FROM   buyer_mgmt.buyer_code_buyers bcb
                JOIN   user_mgmt.user_buyers ub  ON ub.buyer_id = bcb.buyer_id
                JOIN   identity.users u           ON u.id        = ub.user_id
                LEFT JOIN identity.accounts a      ON a.user_id   = u.id
                WHERE  bcb.buyer_code_id = ?
                  AND  u.active = TRUE
                ORDER BY a.full_name NULLS LAST, u.id
                """,
                (rs, rowNum) -> {
                    long userId = rs.getLong("user_id");
                    String fullName = rs.getString("full_name");
                    String email = rs.getString("email");
                    String display = fullName;
                    if (display == null || display.isBlank()) {
                        display = (email != null && !email.isBlank()) ? email : ("User #" + userId);
                    }
                    return new BuyerUserOption(userId, display, email);
                },
                buyerCodeId);
    }

    /**
     * Single-shot draft creation + on-behalf stamping. Steps:
     *  1. Validate (orderNumber, buyerCodeId) and that the chosen
     *     {@code onBehalfOfUserId} actually maps to a buyer on the code
     *     (defence-in-depth — the modal can't surface a stale user
     *     post-page-load).
     *  2. Call {@link CreditRequestService#createDraft} with
     *     {@code isAdmin=true} — the rep is acting as a privileged
     *     actor for this buyer-code, the eligibility check above
     *     replaces the buyer-code-ownership junction guard.
     *  3. Stamp {@code is_on_behalf=TRUE},
     *     {@code on_behalf_of_id=<chosen user>},
     *     {@code on_behalf_buyer_code_id=<buyerCodeId>}.
     */
    @Transactional
    public CreditRequest createDraftOnBehalf(
            Long salesRepUserId,
            String orderNumber,
            Long buyerCodeId,
            Long onBehalfOfUserId) {
        if (orderNumber == null || orderNumber.isBlank()) {
            throw new IllegalArgumentException("orderNumber is required");
        }
        if (buyerCodeId == null) {
            throw new IllegalArgumentException("buyerCodeId is required");
        }
        if (onBehalfOfUserId == null) {
            throw new IllegalArgumentException("onBehalfOfUserId is required");
        }

        boolean userBelongsToCode = listBuyersForCode(salesRepUserId, buyerCodeId).stream()
                .anyMatch(b -> onBehalfOfUserId.equals(b.userId()));
        if (!userBelongsToCode) {
            throw new SecurityException(
                    "User " + onBehalfOfUserId + " is not associated with buyer_code_id=" + buyerCodeId);
        }

        // isAdmin=true bypasses the buyer-code-ownership junction check
        // in CreditRequestService — the rep is acting on the buyer's
        // behalf and we've already validated they have a legitimate
        // reason to touch this code (Phase 1: SalesRep role + the
        // user-belongs-to-code check above).
        CreditRequest draft = creditRequestService.createDraft(
                orderNumber, buyerCodeId, salesRepUserId, /* isAdmin */ true);

        draft.setIsOnBehalf(Boolean.TRUE);
        draft.setOnBehalfOfId(onBehalfOfUserId);
        draft.setOnBehalfBuyerCodeId(buyerCodeId);
        return creditRequestRepository.save(draft);
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }
}
