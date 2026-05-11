package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.EncumberedDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.MissingDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.WrongDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.enums.ShipStatus;
import com.ecoatm.salesplatform.model.partialcredit.enums.ShipmentDamaged;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestStatusRepository;
import com.ecoatm.salesplatform.repository.partialcredit.EncumberedDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.MissingDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.WrongDeviceLineRepository;
import com.ecoatm.salesplatform.service.partialcredit.snowflake.CreditRequestSnowflakeReader;
import com.ecoatm.salesplatform.service.partialcredit.snowflake.CreditRequestSnowflakeReader.ManifestLine;
import com.ecoatm.salesplatform.service.partialcredit.snowflake.CreditRequestSnowflakeReader.OrderHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditRequestServiceTest {

    private static final String BUYER_CODE = "NB_PWS";
    private static final Long BUYER_CODE_ID = 100L;
    private static final Long USER_ID = 7L;
    private static final String ORDER = "SO-001";

    @Mock CreditRequestRepository creditRequestRepository;
    @Mock CreditRequestStatusRepository statusRepository;
    @Mock MissingDeviceLineRepository missingDeviceLineRepository;
    @Mock WrongDeviceLineRepository wrongDeviceLineRepository;
    @Mock EncumberedDeviceLineRepository encumberedDeviceLineRepository;
    @Mock BuyerCodeRepository buyerCodeRepository;
    @Mock CreditRequestSnowflakeReader snowflakeReader;
    @Mock CreditRequestValidator validator;
    @Mock JdbcTemplate jdbcTemplate;

    CreditRequestService service;

    @BeforeEach
    void setUp() {
        service = new CreditRequestService(
                creditRequestRepository, statusRepository,
                missingDeviceLineRepository, wrongDeviceLineRepository, encumberedDeviceLineRepository,
                buyerCodeRepository, snowflakeReader, validator, jdbcTemplate);
    }

    // ─── createDraft ───────────────────────────────────────────────────

    @Test
    @DisplayName("createDraft enriches the request with party_name + shipped date from Snowflake")
    void createDraft_enrichesFromSnowflakeHeader() {
        primeBuyerCodeOwnership(true);
        primeBuyerCodeLookup();
        primeStatus(SystemStatus.DRAFT, 1L);
        when(snowflakeReader.getOrderHeader(ORDER, BUYER_CODE))
                .thenReturn(Optional.of(new OrderHeader(
                        ORDER, BUYER_CODE, "Acme Corp",
                        Instant.parse("2026-04-01T00:00:00Z"),
                        Instant.parse("2026-04-05T00:00:00Z"))));
        when(creditRequestRepository.save(any(CreditRequest.class)))
                .thenAnswer(inv -> { CreditRequest c = inv.getArgument(0); c.setId(50L); return c; });

        CreditRequest cr = service.createDraft(ORDER, BUYER_CODE_ID, USER_ID, false);

        assertThat(cr.getOrderNumber()).isEqualTo(ORDER);
        assertThat(cr.getPartyName()).isEqualTo("Acme Corp");
        assertThat(cr.getOrderShippedDate()).isEqualTo(Instant.parse("2026-04-05T00:00:00Z"));
        assertThat(cr.getStatusId()).isEqualTo(1L);
        assertThat(cr.getShipmentDamaged()).isEqualTo(ShipmentDamaged.NOT_ANSWERED);
    }

    @Test
    @DisplayName("createDraft tolerates an empty Snowflake header (logging reader / unknown order)")
    void createDraft_emptyHeader_persistsBareRow() {
        primeBuyerCodeOwnership(true);
        primeBuyerCodeLookup();
        primeStatus(SystemStatus.DRAFT, 1L);
        when(snowflakeReader.getOrderHeader(ORDER, BUYER_CODE)).thenReturn(Optional.empty());
        when(creditRequestRepository.save(any(CreditRequest.class)))
                .thenAnswer(inv -> { CreditRequest c = inv.getArgument(0); c.setId(51L); return c; });

        CreditRequest cr = service.createDraft(ORDER, BUYER_CODE_ID, USER_ID, false);

        assertThat(cr.getPartyName()).isNull();
        assertThat(cr.getOrderShippedDate()).isNull();
    }

    @Test
    @DisplayName("createDraft blocks non-admins who don't own the buyer code")
    void createDraft_unauthorizedUser_throws() {
        primeBuyerCodeOwnership(false);

        assertThatThrownBy(() -> service.createDraft(ORDER, BUYER_CODE_ID, USER_ID, false))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("does not own buyer_code_id=100");
    }

    // ─── update ────────────────────────────────────────────────────────

    @Test
    @DisplayName("update applies the reason flag patch and re-stamps changed_by")
    void update_appliesPatch() {
        CreditRequest existing = newDraft();
        when(creditRequestRepository.findById(99L)).thenReturn(Optional.of(existing));
        primeBuyerCodeOwnership(true);
        primeStatusFor(existing.getStatusId(), SystemStatus.DRAFT);
        when(creditRequestRepository.save(any(CreditRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        CreditRequest updated = service.update(
                99L, USER_ID, false,
                Boolean.TRUE, null, Boolean.TRUE, ShipmentDamaged.NO);

        assertThat(updated.getHasMissingDevice()).isTrue();
        assertThat(updated.getHasEncumberedDevice()).isTrue();
        assertThat(updated.getHasWrongDevice()).isFalse();
        assertThat(updated.getShipmentDamaged()).isEqualTo(ShipmentDamaged.NO);
        assertThat(updated.getChangedById()).isEqualTo(USER_ID);
    }

    @Test
    @DisplayName("update on a non-DRAFT request fails fast")
    void update_nonDraftStatus_throws() {
        CreditRequest existing = newDraft();
        existing.setStatusId(2L);
        when(creditRequestRepository.findById(99L)).thenReturn(Optional.of(existing));
        primeBuyerCodeOwnership(true);
        primeStatusFor(2L, SystemStatus.PENDING_APPROVAL);

        assertThatThrownBy(() -> service.update(
                99L, USER_ID, false, Boolean.TRUE, null, null, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not in DRAFT");
    }

    // ─── submit (validator + denormalisation) ──────────────────────────

    @Test
    @DisplayName("submit denormalises manifest fields onto every missing-device line")
    void submit_denormalisesMissingLines() {
        CreditRequest draft = newDraft();
        draft.setHasMissingDevice(true);
        primeForSubmit(draft);

        // Manifest carries the brand/model/grade we expect to land on the line.
        when(snowflakeReader.getOrderLines(ORDER, BUYER_CODE))
                .thenReturn(List.of(new ManifestLine(
                        "BC-1", "IMEI-1", "Apple", "iPhone 13", "A_YYY",
                        "EC-1", "BOX-1", new BigDecimal("450.00"), "TRK-123")));

        MissingDeviceLine line = new MissingDeviceLine();
        line.setId(900L);
        line.setBarcodeSubmitted("BC-1");
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(draft.getId()))
                .thenReturn(List.of(line));
        when(wrongDeviceLineRepository.findByCreditRequestIdOrderById(draft.getId()))
                .thenReturn(List.of());
        when(encumberedDeviceLineRepository.findByCreditRequestIdOrderById(draft.getId()))
                .thenReturn(List.of());

        service.submit(draft.getId(), USER_ID, false);

        ArgumentCaptor<MissingDeviceLine> savedLine = ArgumentCaptor.forClass(MissingDeviceLine.class);
        verify(missingDeviceLineRepository).save(savedLine.capture());
        MissingDeviceLine result = savedLine.getValue();
        assertThat(result.getBrand()).isEqualTo("Apple");
        assertThat(result.getModel()).isEqualTo("iPhone 13");
        assertThat(result.getGrade()).isEqualTo("A_YYY");
        assertThat(result.getBoxNumber()).isEqualTo("BOX-1");
        assertThat(result.getAmountPaid()).isEqualByComparingTo("450.00");
        assertThat(result.getShipStatus()).isEqualTo(ShipStatus.SHIPPED);
    }

    @Test
    @DisplayName("submit marks missing lines NOT_SHIPPED when manifest tracking_number is blank")
    void submit_blankTracking_marksNotShipped() {
        CreditRequest draft = newDraft();
        draft.setHasMissingDevice(true);
        primeForSubmit(draft);
        when(snowflakeReader.getOrderLines(ORDER, BUYER_CODE))
                .thenReturn(List.of(new ManifestLine(
                        "BC-2", null, "Apple", "iPhone 13", "B_YYY",
                        "EC-1", "BOX-1", new BigDecimal("400.00"), "  ")));
        MissingDeviceLine line = new MissingDeviceLine();
        line.setId(901L);
        line.setBarcodeSubmitted("BC-2");
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(draft.getId()))
                .thenReturn(List.of(line));
        when(wrongDeviceLineRepository.findByCreditRequestIdOrderById(draft.getId())).thenReturn(List.of());
        when(encumberedDeviceLineRepository.findByCreditRequestIdOrderById(draft.getId())).thenReturn(List.of());

        service.submit(draft.getId(), USER_ID, false);

        ArgumentCaptor<MissingDeviceLine> savedLine = ArgumentCaptor.forClass(MissingDeviceLine.class);
        verify(missingDeviceLineRepository).save(savedLine.capture());
        assertThat(savedLine.getValue().getShipStatus()).isEqualTo(ShipStatus.NOT_SHIPPED);
    }

    @Test
    @DisplayName("submit denormalises Expected* fields onto wrong-device lines")
    void submit_denormalisesWrongLines() {
        CreditRequest draft = newDraft();
        draft.setHasWrongDevice(true);
        primeForSubmit(draft);
        when(snowflakeReader.getOrderLines(ORDER, BUYER_CODE))
                .thenReturn(List.of(new ManifestLine(
                        "BC-9", "IMEI-9", "Samsung", "Galaxy S22", "A_NNN",
                        "EC-9", "BOX-2", new BigDecimal("600.00"), "TRK-9")));
        WrongDeviceLine line = new WrongDeviceLine();
        line.setId(902L);
        line.setExpectedBarcode("BC-9");
        when(wrongDeviceLineRepository.findByCreditRequestIdOrderById(draft.getId()))
                .thenReturn(List.of(line));
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(draft.getId())).thenReturn(List.of());
        when(encumberedDeviceLineRepository.findByCreditRequestIdOrderById(draft.getId())).thenReturn(List.of());

        service.submit(draft.getId(), USER_ID, false);

        ArgumentCaptor<WrongDeviceLine> savedLine = ArgumentCaptor.forClass(WrongDeviceLine.class);
        verify(wrongDeviceLineRepository).save(savedLine.capture());
        WrongDeviceLine result = savedLine.getValue();
        assertThat(result.getExpectedBrand()).isEqualTo("Samsung");
        assertThat(result.getExpectedModel()).isEqualTo("Galaxy S22");
        assertThat(result.getExpectedEcoatmCode()).isEqualTo("EC-9");
        assertThat(result.getExpectedAmountPaid()).isEqualByComparingTo("600.00");
    }

    @Test
    @DisplayName("submit flips status to PENDING_APPROVAL and stamps submitted_by/date")
    void submit_flipsStatus() {
        CreditRequest draft = newDraft();
        draft.setHasMissingDevice(true);
        primeForSubmit(draft);
        // Empty manifest short-circuits denormalisation — line repos go unused.
        when(snowflakeReader.getOrderLines(ORDER, BUYER_CODE)).thenReturn(List.of());

        CreditRequest submitted = service.submit(draft.getId(), USER_ID, false);

        assertThat(submitted.getStatusId()).isEqualTo(2L);
        assertThat(submitted.getSubmittedById()).isEqualTo(USER_ID);
        assertThat(submitted.getSubmittedDate()).isNotNull();
    }

    @Test
    @DisplayName("submit aborts on validator failure without writing the status flip")
    void submit_validatorFailure_skipsStatusFlip() {
        CreditRequest draft = newDraft();
        when(creditRequestRepository.findById(draft.getId())).thenReturn(Optional.of(draft));
        primeBuyerCodeOwnership(true);
        primeStatusFor(draft.getStatusId(), SystemStatus.DRAFT);
        primeBuyerCodeLookup();
        org.mockito.Mockito.doThrow(new CreditRequestValidationException(
                        List.of(ValidationIssue.noReasonSelected())))
                .when(validator).validateForSubmitOrThrow(any(), eq(BUYER_CODE));

        assertThatThrownBy(() -> service.submit(draft.getId(), USER_ID, false))
                .isInstanceOf(CreditRequestValidationException.class);

        // No save call after the validator throws.
        org.mockito.Mockito.verify(creditRequestRepository, org.mockito.Mockito.never())
                .save(any(CreditRequest.class));
    }

    // ─── helpers ───────────────────────────────────────────────────────

    private CreditRequest newDraft() {
        CreditRequest cr = new CreditRequest();
        cr.setId(99L);
        cr.setRequestNumber("PCR-X");
        cr.setOrderNumber(ORDER);
        cr.setBuyerCodeId(BUYER_CODE_ID);
        cr.setStatusId(1L);
        cr.setHasMissingDevice(false);
        cr.setHasWrongDevice(false);
        cr.setHasEncumberedDevice(false);
        return cr;
    }

    private void primeForSubmit(CreditRequest draft) {
        when(creditRequestRepository.findById(draft.getId())).thenReturn(Optional.of(draft));
        primeBuyerCodeOwnership(true);
        primeStatusFor(draft.getStatusId(), SystemStatus.DRAFT);
        primeBuyerCodeLookup();
        when(statusRepository.findBySystemStatus(SystemStatus.PENDING_APPROVAL))
                .thenReturn(Optional.of(statusRow(2L, SystemStatus.PENDING_APPROVAL)));
        when(creditRequestRepository.save(any(CreditRequest.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private void primeBuyerCodeOwnership(boolean owns) {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), any(), any()))
                .thenReturn(owns ? 1L : 0L);
    }

    private void primeBuyerCodeLookup() {
        BuyerCode bc = new BuyerCode();
        bc.setId(BUYER_CODE_ID);
        bc.setCode(BUYER_CODE);
        when(buyerCodeRepository.findById(BUYER_CODE_ID)).thenReturn(Optional.of(bc));
    }

    private void primeStatus(SystemStatus s, Long id) {
        when(statusRepository.findBySystemStatus(s)).thenReturn(Optional.of(statusRow(id, s)));
    }

    private void primeStatusFor(Long statusId, SystemStatus s) {
        when(statusRepository.findById(statusId)).thenReturn(Optional.of(statusRow(statusId, s)));
    }

    private static CreditRequestStatus statusRow(Long id, SystemStatus s) {
        CreditRequestStatus r = new CreditRequestStatus();
        r.setId(id);
        r.setSystemStatus(s);
        r.setExternalStatusText(s.name());
        return r;
    }
}
