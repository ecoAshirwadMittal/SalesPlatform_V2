package com.ecoatm.salesplatform.service.rma;

import com.ecoatm.salesplatform.dto.OracleResponse;
import com.ecoatm.salesplatform.model.User;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.pws.Rma;
import com.ecoatm.salesplatform.model.pws.RmaItem;
import com.ecoatm.salesplatform.repository.UserRepository;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.repository.pws.RmaItemRepository;
import com.ecoatm.salesplatform.repository.pws.RmaRepository;
import com.ecoatm.salesplatform.service.BuyerCodeLookupService;
import com.ecoatm.salesplatform.service.OracleOrderClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link RmaOracleService#createRmaInOracle} — the shared
 * "build payload → submitRma → write oracle_* columns" core reused by the
 * AFTER_COMMIT Oracle-create listener and the admin /resubmit-oracle endpoint.
 *
 * <p>Mirrors the write logic of the legacy {@code ACT_RMADetails_CompleteReview}
 * success/failure branches: on {@code returnCode == "00"} the Oracle
 * number/id/http/json/status land and {@code is_successful = true}; on a
 * blank/failed returnCode {@code is_successful = false} with the status/message
 * captured for the resubmit path to recover — the review is never affected.
 */
@ExtendWith(MockitoExtension.class)
class RmaOracleServiceTest {

    @Mock private RmaRepository rmaRepository;
    @Mock private RmaItemRepository rmaItemRepository;
    @Mock private DeviceRepository deviceRepository;
    @Mock private BuyerCodeLookupService buyerCodeLookup;
    @Mock private RmaOraclePayloadBuilder payloadBuilder;
    @Mock private OracleOrderClient oracleOrderClient;
    @Mock private UserRepository userRepository;

    private RmaOracleService service;

    @BeforeEach
    void setUp() {
        service = new RmaOracleService(rmaRepository, rmaItemRepository, deviceRepository,
                buyerCodeLookup, payloadBuilder, oracleOrderClient, userRepository);
    }

    private Rma rma(Long id, Long buyerCodeId, Long reviewedBy) {
        Rma rma = new Rma();
        rma.setId(id);
        rma.setNumber("RMA-" + id);
        rma.setBuyerCodeId(buyerCodeId);
        rma.setReviewedByUserId(reviewedBy);
        return rma;
    }

    private RmaItem item(Long id, String imei, Long deviceId, BigDecimal price, String status) {
        RmaItem it = new RmaItem();
        it.setId(id);
        it.setImei(imei);
        it.setDeviceId(deviceId);
        it.setSalePrice(price);
        it.setStatus(status);
        return it;
    }

    private OracleResponse response(String returnCode, String returnMessage, String number,
                                    String id, Integer httpCode, String json) {
        OracleResponse r = new OracleResponse();
        r.setReturnCode(returnCode);
        r.setReturnMessage(returnMessage);
        r.setOrderNumber(number);
        r.setOrderId(id);
        r.setHttpCode(httpCode);
        r.setJsonResponse(json);
        return r;
    }

    @Test
    @DisplayName("success (returnCode 00): oracle_* columns + is_successful=true written; SIM number persisted")
    void success_writesOracleColumns() {
        Rma rma = rma(1L, 100L, 7L);
        RmaItem approved = item(10L, "IMEI-1", 55L, new BigDecimal("175"), "Approve");

        when(rmaRepository.findById(1L)).thenReturn(Optional.of(rma));
        when(rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(1L)).thenReturn(List.of(approved));
        Device device = new Device();
        device.setId(55L);
        device.setSku("SKU-55");
        when(deviceRepository.findAllById(anyIterable())).thenReturn(List.of(device));
        when(buyerCodeLookup.findCodeById(100L)).thenReturn("BC001");
        User reviewer = new User();
        reviewer.setName("sales.rep");
        when(userRepository.findById(7L)).thenReturn(Optional.of(reviewer));
        when(payloadBuilder.build(eq(rma), eq(List.of(approved)), anyMap(), eq("BC001"), eq("sales.rep")))
                .thenReturn("PAYLOAD-JSON");
        when(oracleOrderClient.submitRma("PAYLOAD-JSON"))
                .thenReturn(response("00", "Toggle off — simulated", "SIM-999", "OID-1", 200, "{\"ok\":true}"));

        service.createRmaInOracle(1L);

        assertThat(rma.getJsonContent()).isEqualTo("PAYLOAD-JSON");
        assertThat(rma.getIsSuccessful()).isTrue();
        assertThat(rma.getOracleNumber()).isEqualTo("SIM-999");
        assertThat(rma.getOracleId()).isEqualTo("OID-1");
        assertThat(rma.getOracleHttpCode()).isEqualTo(200);
        assertThat(rma.getOracleJsonResponse()).isEqualTo("{\"ok\":true}");
        assertThat(rma.getOracleRmaStatus()).isEqualTo("Toggle off — simulated");
        verify(rmaRepository).save(rma);
    }

    @Test
    @DisplayName("failure (blank returnCode): is_successful=false, status captured, no oracle number; no exception")
    void failure_capturesStatus_noOracleNumber() {
        Rma rma = rma(2L, 100L, 7L);
        RmaItem approved = item(11L, "IMEI-2", 56L, new BigDecimal("200"), "Approve");

        when(rmaRepository.findById(2L)).thenReturn(Optional.of(rma));
        when(rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(2L)).thenReturn(List.of(approved));
        when(deviceRepository.findAllById(anyIterable())).thenReturn(List.of());
        when(buyerCodeLookup.findCodeById(100L)).thenReturn("BC001");
        when(userRepository.findById(7L)).thenReturn(Optional.empty());
        when(payloadBuilder.build(any(), anyList(), anyMap(), any(), any())).thenReturn("PAYLOAD");
        when(oracleOrderClient.submitRma("PAYLOAD"))
                .thenReturn(response(null, "Oracle API is disabled", null, null, null, null));

        service.createRmaInOracle(2L);

        assertThat(rma.getIsSuccessful()).isFalse();
        assertThat(rma.getOracleRmaStatus()).isEqualTo("Oracle API is disabled");
        assertThat(rma.getOracleNumber()).isNull();
        assertThat(rma.getOracleId()).isNull();
        verify(rmaRepository).save(rma);
    }

    @Test
    @DisplayName("only APPROVED items feed the payload; declined lines are excluded")
    void onlyApprovedItemsInPayload() {
        Rma rma = rma(3L, 100L, 7L);
        RmaItem approved = item(12L, "A", 60L, new BigDecimal("100"), "Approve");
        RmaItem declined = item(13L, "D", 61L, new BigDecimal("50"), "Decline");

        when(rmaRepository.findById(3L)).thenReturn(Optional.of(rma));
        when(rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(3L))
                .thenReturn(List.of(approved, declined));
        when(deviceRepository.findAllById(anyIterable())).thenReturn(List.of());
        when(buyerCodeLookup.findCodeById(100L)).thenReturn("BC001");
        when(userRepository.findById(7L)).thenReturn(Optional.empty());
        when(payloadBuilder.build(any(), anyList(), anyMap(), any(), any())).thenReturn("P");
        when(oracleOrderClient.submitRma("P")).thenReturn(response("00", "ok", "SIM-1", null, 200, "{}"));

        service.createRmaInOracle(3L);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RmaItem>> itemsCaptor = ArgumentCaptor.forClass(List.class);
        verify(payloadBuilder).build(eq(rma), itemsCaptor.capture(), anyMap(), any(), any());
        assertThat(itemsCaptor.getValue()).containsExactly(approved);
    }

    @Test
    @DisplayName("RMA not found throws IllegalArgumentException; submitRma never called")
    void notFound_throws() {
        when(rmaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createRmaInOracle(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("999");
        verify(oracleOrderClient, never()).submitRma(anyString());
    }
}
