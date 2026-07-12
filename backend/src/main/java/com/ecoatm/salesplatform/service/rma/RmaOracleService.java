package com.ecoatm.salesplatform.service.rma;

import com.ecoatm.salesplatform.dto.OracleResponse;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.pws.Rma;
import com.ecoatm.salesplatform.model.pws.RmaItem;
import com.ecoatm.salesplatform.repository.UserRepository;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.repository.pws.RmaItemRepository;
import com.ecoatm.salesplatform.repository.pws.RmaRepository;
import com.ecoatm.salesplatform.service.BuyerCodeLookupService;
import com.ecoatm.salesplatform.service.OracleOrderClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Sends a reviewed RMA to Oracle and records the response on the {@code rma}
 * row. This is the shared "build payload → {@code submitRma} → write
 * {@code oracle_*} columns" core, reused by two callers:
 * <ul>
 *   <li>{@code RmaOracleCreateListener} — the AFTER_COMMIT {@code @Async}
 *       reaction to an APPROVED {@code RmaReviewCompletedEvent}.</li>
 *   <li>{@code RmaController} {@code POST /{rmaId}/resubmit-oracle} — the admin
 *       recovery path for an RMA whose prior create failed
 *       ({@code ACT_RMA_ReSubmitToOracle}).</li>
 * </ul>
 *
 * <p><b>Transaction shape (the readOnly-tx gotcha).</b> {@link #createRmaInOracle}
 * is {@code @Transactional(REQUIRES_NEW)} and deliberately NOT {@code readOnly}:
 * it WRITES the {@code oracle_*} columns. {@code REQUIRES_NEW} means the write
 * runs in its own transaction, fully isolated from the already-committed
 * review-completion transaction — so a failed Oracle create can never roll the
 * review back. When invoked from the listener (itself on the {@code @Async}
 * thread after commit) this new transaction is where the columns land; when
 * invoked from the controller it opens a normal request-scoped write
 * transaction.
 *
 * <p>Mirrors the write logic of {@code ACT_RMADetails_CompleteReview} /
 * {@code ACT_RMA_ReSubmitToOracle}: {@code returnCode == "00"} persists the
 * Oracle number/id/http/json/status with {@code is_successful = true}; a
 * blank/non-{@code "00"} returnCode captures the status/message/http/json with
 * {@code is_successful = false} (no Oracle number) so the resubmit path can
 * recover it. {@link OracleOrderClient#submitRma} never throws, so the failed
 * path is not an exception — the review stays APPROVED with a failed Oracle
 * status.
 */
@Service
public class RmaOracleService {

    private static final Logger log = LoggerFactory.getLogger(RmaOracleService.class);

    /** {@code ENUM_RMAItemStatus.Approve} — only approved lines go to Oracle. */
    private static final String STATUS_APPROVE = "Approve";
    /** Oracle "success" return code (Mendix {@code ReturnCode = '00'}). */
    private static final String RETURN_CODE_SUCCESS = "00";

    private final RmaRepository rmaRepository;
    private final RmaItemRepository rmaItemRepository;
    private final DeviceRepository deviceRepository;
    private final BuyerCodeLookupService buyerCodeLookup;
    private final RmaOraclePayloadBuilder payloadBuilder;
    private final OracleOrderClient oracleOrderClient;
    private final UserRepository userRepository;

    public RmaOracleService(RmaRepository rmaRepository,
                            RmaItemRepository rmaItemRepository,
                            DeviceRepository deviceRepository,
                            BuyerCodeLookupService buyerCodeLookup,
                            RmaOraclePayloadBuilder payloadBuilder,
                            OracleOrderClient oracleOrderClient,
                            UserRepository userRepository) {
        this.rmaRepository = rmaRepository;
        this.rmaItemRepository = rmaItemRepository;
        this.deviceRepository = deviceRepository;
        this.buyerCodeLookup = buyerCodeLookup;
        this.payloadBuilder = payloadBuilder;
        this.oracleOrderClient = oracleOrderClient;
        this.userRepository = userRepository;
    }

    /**
     * Build the Oracle create-RMA payload for {@code rmaId}, POST it via
     * {@link OracleOrderClient#submitRma}, and persist the {@code oracle_*}
     * response columns. Returns the updated (and saved) {@link Rma}.
     *
     * @throws IllegalArgumentException if no RMA exists for {@code rmaId}
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Rma createRmaInOracle(Long rmaId) {
        Rma rma = rmaRepository.findById(rmaId)
                .orElseThrow(() -> new IllegalArgumentException("RMA not found: " + rmaId));

        List<RmaItem> approvedItems = rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(rmaId).stream()
                .filter(item -> STATUS_APPROVE.equals(item.getStatus()))
                .toList();

        Map<Long, String> skuByDeviceId = resolveSkus(approvedItems);
        String buyerCode = buyerCodeLookup.findCodeById(rma.getBuyerCodeId());
        String originSystemUser = resolveReviewerName(rma.getReviewedByUserId());

        String payload = payloadBuilder.build(rma, approvedItems, skuByDeviceId, buyerCode, originSystemUser);
        rma.setJsonContent(payload);

        OracleResponse response = oracleOrderClient.submitRma(payload);
        applyResponse(rma, response);

        rmaRepository.save(rma);
        // No token, basic-auth or PII in this log line — only the RMA number and
        // Oracle status envelope (business data).
        log.info("RMA {} Oracle create result — success={}, httpCode={}, oracleNumber={}",
                rma.getNumber(), rma.getIsSuccessful(), rma.getOracleHttpCode(),
                rma.getOracleNumber());
        return rma;
    }

    /**
     * Copy the Oracle response envelope onto the RMA. On a {@code "00"} return
     * code the Oracle number/id are recorded and {@code is_successful = true};
     * otherwise only the status/http/json are recorded ({@code is_successful =
     * false}) so the resubmit path can retry without a stale Oracle number.
     */
    private void applyResponse(Rma rma, OracleResponse response) {
        rma.setOracleHttpCode(response.getHttpCode());
        rma.setOracleJsonResponse(response.getJsonResponse());
        rma.setOracleRmaStatus(response.getReturnMessage());

        boolean success = RETURN_CODE_SUCCESS.equals(response.getReturnCode());
        rma.setIsSuccessful(success);
        if (success) {
            rma.setOracleNumber(response.getOrderNumber());
            rma.setOracleId(response.getOrderId());
        }
    }

    /** Device-id → SKU lookup for the approved items' {@code itemNumber} field. */
    private Map<Long, String> resolveSkus(List<RmaItem> approvedItems) {
        Set<Long> deviceIds = approvedItems.stream()
                .map(RmaItem::getDeviceId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        if (deviceIds.isEmpty()) {
            return Map.of();
        }
        return deviceRepository.findAllById(deviceIds).stream()
                .filter(device -> device.getSku() != null)
                .collect(Collectors.toMap(Device::getId, Device::getSku, (a, b) -> a));
    }

    /**
     * Resolve the reviewer's login name for the payload's {@code originSystemUser}
     * (Mendix {@code $currentUser/Name}). Best-effort: a null/unknown id falls
     * back to a non-PII placeholder rather than failing the Oracle send.
     */
    private String resolveReviewerName(Long reviewedByUserId) {
        if (reviewedByUserId == null) {
            return "system";
        }
        return userRepository.findById(reviewedByUserId)
                .map(com.ecoatm.salesplatform.model.User::getName)
                .filter(name -> name != null && !name.isBlank())
                .orElse("user-" + reviewedByUserId);
    }
}
