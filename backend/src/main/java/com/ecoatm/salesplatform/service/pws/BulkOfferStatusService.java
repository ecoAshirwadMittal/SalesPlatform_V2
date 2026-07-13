package com.ecoatm.salesplatform.service.pws;

import com.ecoatm.salesplatform.dto.ChangeOfferStatusRequest;
import com.ecoatm.salesplatform.dto.ChangeOfferStatusResult;
import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.Order;
import com.ecoatm.salesplatform.repository.pws.OfferRepository;
import com.ecoatm.salesplatform.repository.pws.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Administrator-only bulk offer-status change tool — a faithful port of the
 * Mendix {@code ACT_ChangeOfferStatus_Proceed} action (gap-analysis 2.3
 * sub-feature 3). Used as an ops-correction lever to fix offer status in bulk
 * after a bad Oracle sync.
 *
 * <p><b>Locked behaviour (user decisions):</b>
 * <ul>
 *   <li><b>Permissive any-&gt;any</b> — no transition allowlist. The only guards
 *       are the {@link ChangeOfferStatusValidator} input check and, on the
 *       date-range path, the from-status match.</li>
 *   <li><b>Side-effect-free</b> — this changes offer status and writes exactly
 *       one audit row. It deliberately performs <i>no</i> Oracle re-send, email,
 *       or inventory reservation.</li>
 *   <li><b>Audit-logged</b> — one {@code pws.admin_audit_log} row per invocation,
 *       stamped with the JWT-derived caller (passed in as {@code actor}, never
 *       taken from the request) and the matched/changed counts. Reuses the
 *       existing audit-writer pattern (see
 *       {@code PricingService#softDeleteDevice}).</li>
 * </ul>
 *
 * <p><b>Metadata-only path — rejected:</b> the legacy tool wrote
 * {@code HasShipmentDetails} (and re-set {@code LegacyOrder} to its own value — a
 * no-op) on the resolved orders. The modern {@code pws.order} table has neither
 * column, and this feature adds no migration (locked decision), so the flag write
 * is impossible. Rather than pretend success — an earlier {@code applyMetadata}
 * path bumped {@code updated_date} and returned {@code metadataOnly:true} without
 * persisting anything — a {@code notOrderStatusChange=true} request is now
 * rejected up front by {@link ChangeOfferStatusValidator} (→ HTTP 400), keeping
 * the gap visible until a schema-prep migration lands. See the 2.3.E
 * metadata-fix report.
 */
@Service
public class BulkOfferStatusService {

    private static final Logger log = LoggerFactory.getLogger(BulkOfferStatusService.class);

    private static final String AUDIT_INSERT =
            "INSERT INTO pws.admin_audit_log "
            + "(entity_type, entity_id, action, reason, actor, before_state, after_state) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    /** A bulk operation has no single target entity id; the audit column is NOT NULL. */
    private static final long BULK_ENTITY_ID = 0L;

    private final OrderRepository orderRepository;
    private final OfferRepository offerRepository;
    private final ChangeOfferStatusValidator validator;
    private final JdbcTemplate jdbc;

    public BulkOfferStatusService(OrderRepository orderRepository,
                                  OfferRepository offerRepository,
                                  ChangeOfferStatusValidator validator,
                                  JdbcTemplate jdbc) {
        this.orderRepository = orderRepository;
        this.offerRepository = offerRepository;
        this.validator = validator;
        this.jdbc = jdbc;
    }

    /**
     * Validate, resolve the target orders, apply the status change, and write
     * exactly one audit row.
     *
     * @param req   the (already {@code @Valid}-bound) change request
     * @param actor the JWT-derived caller identity for the audit row — the
     *              controller resolves this from {@code Authentication}; it must
     *              never come from the request body
     * @throws IllegalArgumentException when {@code req} is not a valid bulk
     *         change — including a {@code notOrderStatusChange=true} metadata-only
     *         request, which is unsupported on the modern schema (→ HTTP 400)
     */
    @Transactional
    public ChangeOfferStatusResult changeStatus(ChangeOfferStatusRequest req, String actor) {
        validator.validate(req);

        List<Order> orders = resolveOrders(req);
        ChangeOfferStatusResult result = applyStatusChange(req, orders, actor);

        writeAudit(req, result, actor);
        log.info("Bulk offer-status change applied: matchedOrders={}, changedOffers={}",
                result.matchedOrders(), result.changedOffers());
        return result;
    }

    /** Port of {@code SUB_ChangeOfferStatus_GetOrderList}. */
    private List<Order> resolveOrders(ChangeOfferStatusRequest req) {
        if (req.allPeriod()) {
            // The explicitly-selected orders (legacy: the attached
            // ChangeOfferStatusHelper_Order association).
            return orderRepository.findAllById(req.orderIds());
        }
        // trimToDays(OrderDate) BETWEEN startingDate AND endingDate, expressed as
        // a half-open range so the whole ending day is included.
        LocalDateTime start = req.startingDate().atStartOfDay();
        LocalDateTime endExclusive = req.endingDate().plusDays(1).atStartOfDay();
        return orderRepository.findByOrderDateWithinRange(start, endExclusive);
    }

    /**
     * Status-change path. {@code allPeriod} changes every linked offer;
     * otherwise only offers whose current status equals {@code fromOfferStatus}
     * (the safety guard) are changed.
     */
    private ChangeOfferStatusResult applyStatusChange(ChangeOfferStatusRequest req, List<Order> orders,
                                                      String actor) {
        List<Long> offerIds = orders.stream()
                .map(Order::getOffer)
                .filter(Objects::nonNull)
                .map(Offer::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<Offer> offers = offerRepository.findAllById(offerIds);

        List<Offer> target = req.allPeriod()
                ? offers
                : offers.stream()
                        .filter(o -> req.fromOfferStatus().equals(o.getStatus()))
                        .toList();

        for (Offer offer : target) {
            offer.setStatus(req.toOrderStatus());
            offer.setChangedBy(actor);
        }
        offerRepository.saveAll(target);

        return new ChangeOfferStatusResult(orders.size(), target.size(), false);
    }

    private void writeAudit(ChangeOfferStatusRequest req, ChangeOfferStatusResult result, String actor) {
        String scope = req.allPeriod()
                ? "allPeriod(orderIds=" + req.orderIds().size() + ")"
                : "dateRange[" + req.startingDate() + ".." + req.endingDate() + "]";
        String reason = "scope=" + scope
                + ", fromOfferStatus=" + req.fromOfferStatus()
                + ", toOrderStatus=" + req.toOrderStatus()
                + ", notOrderStatusChange=" + req.notOrderStatusChange()
                + ", hasShipmentDetails=" + req.hasShipmentDetails()
                + ", matchedOrders=" + result.matchedOrders()
                + ", changedOffers=" + result.changedOffers();
        String before = "fromOfferStatus=" + req.fromOfferStatus();
        String after = "toOrderStatus=" + req.toOrderStatus() + ", changedOffers=" + result.changedOffers();

        jdbc.update(AUDIT_INSERT, "Offer", BULK_ENTITY_ID, "BULK_STATUS_CHANGE", reason, actor, before, after);
    }
}
