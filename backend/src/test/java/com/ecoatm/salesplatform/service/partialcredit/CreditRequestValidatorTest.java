package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.EncumberedDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.MissingDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.WrongDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.enums.PhotoKind;
import com.ecoatm.salesplatform.model.partialcredit.enums.ShipmentDamaged;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestPhotoRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.repository.partialcredit.EncumberedDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.MissingDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.WrongDeviceLineRepository;
import com.ecoatm.salesplatform.service.partialcredit.snowflake.CreditRequestSnowflakeReader;
import com.ecoatm.salesplatform.service.partialcredit.snowflake.CreditRequestSnowflakeReader.ManifestLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditRequestValidatorTest {

    private static final String BUYER_CODE = "NB_PWS";
    private static final String ORDER_NUMBER = "SO-12345";
    private static final Long BUYER_CODE_ID = 100L;
    private static final Long REQUEST_ID = 42L;

    @Mock CreditRequestRepository creditRequestRepository;
    @Mock MissingDeviceLineRepository missingDeviceLineRepository;
    @Mock WrongDeviceLineRepository wrongDeviceLineRepository;
    @Mock EncumberedDeviceLineRepository encumberedDeviceLineRepository;
    @Mock CreditRequestPhotoRepository photoRepository;
    @Mock CreditRequestSnowflakeReader snowflakeReader;

    CreditRequestValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CreditRequestValidator(
                creditRequestRepository,
                missingDeviceLineRepository,
                wrongDeviceLineRepository,
                encumberedDeviceLineRepository,
                photoRepository,
                snowflakeReader);
    }

    // ─── Order eligibility ─────────────────────────────────────────────

    @Test
    @DisplayName("validateForSubmit accepts a fully valid request")
    void validateForSubmit_happyPath_returnsValid() {
        primeReaderAndRepos();
        CreditRequest cr = baseRequest();
        cr.setHasMissingDevice(true);
        cr.setShipmentDamaged(ShipmentDamaged.NO);
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(REQUEST_ID))
                .thenReturn(List.of(new MissingDeviceLine()));

        SubmissionValidationResult result = validator.validateForSubmit(cr, BUYER_CODE);

        assertThat(result.valid()).isTrue();
        assertThat(result.issues()).isEmpty();
    }

    @Test
    @DisplayName("validateForSubmit flags ORDER_NOT_FOUND when manifest check returns false")
    void validateForSubmit_orderNotInManifest_flagsOrderNotFound() {
        when(snowflakeReader.validateOrderForBuyer(ORDER_NUMBER, BUYER_CODE)).thenReturn(false);
        when(creditRequestRepository.findActiveByOrderAndBuyer(
                eq(ORDER_NUMBER), eq(BUYER_CODE_ID), eq(SystemStatus.DECLINED)))
                .thenReturn(Collections.emptyList());
        CreditRequest cr = baseRequest();
        cr.setHasMissingDevice(true);
        cr.setShipmentDamaged(ShipmentDamaged.NO);
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(REQUEST_ID))
                .thenReturn(List.of(new MissingDeviceLine()));

        SubmissionValidationResult result = validator.validateForSubmit(cr, BUYER_CODE);

        assertThat(codesOf(result)).contains("ORDER_NOT_FOUND");
    }

    @Test
    @DisplayName("validateForSubmit flags ORDER_OUTSIDE_WINDOW when shipped > 30 days ago")
    void validateForSubmit_shippedMoreThan30DaysAgo_flagsOutsideWindow() {
        primeReaderAndRepos();
        CreditRequest cr = baseRequest();
        cr.setHasMissingDevice(true);
        cr.setShipmentDamaged(ShipmentDamaged.NO);
        cr.setOrderShippedDate(Instant.now().minus(35, ChronoUnit.DAYS));
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(REQUEST_ID))
                .thenReturn(List.of(new MissingDeviceLine()));

        SubmissionValidationResult result = validator.validateForSubmit(cr, BUYER_CODE);

        assertThat(codesOf(result)).contains("ORDER_OUTSIDE_WINDOW");
    }

    @Test
    @DisplayName("validateForSubmit accepts shipped-exactly-on-day-30 as within window")
    void validateForSubmit_shippedExactlyAtBoundary_isWithinWindow() {
        primeReaderAndRepos();
        CreditRequest cr = baseRequest();
        cr.setHasMissingDevice(true);
        cr.setShipmentDamaged(ShipmentDamaged.NO);
        cr.setOrderShippedDate(Instant.now().minus(29, ChronoUnit.DAYS));
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(REQUEST_ID))
                .thenReturn(List.of(new MissingDeviceLine()));

        SubmissionValidationResult result = validator.validateForSubmit(cr, BUYER_CODE);

        assertThat(codesOf(result)).doesNotContain("ORDER_OUTSIDE_WINDOW");
    }

    @Test
    @DisplayName("validateForSubmit flags ORDER_HAS_ACTIVE_REQUEST when another non-declined request exists")
    void validateForSubmit_anotherActiveRequest_flagsActive() {
        when(snowflakeReader.validateOrderForBuyer(anyString(), anyString())).thenReturn(true);
        CreditRequest other = new CreditRequest();
        other.setId(99L);
        when(creditRequestRepository.findActiveByOrderAndBuyer(
                eq(ORDER_NUMBER), eq(BUYER_CODE_ID), eq(SystemStatus.DECLINED)))
                .thenReturn(List.of(other));
        CreditRequest cr = baseRequest();
        cr.setHasMissingDevice(true);
        cr.setShipmentDamaged(ShipmentDamaged.NO);
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(REQUEST_ID))
                .thenReturn(List.of(new MissingDeviceLine()));

        SubmissionValidationResult result = validator.validateForSubmit(cr, BUYER_CODE);

        assertThat(codesOf(result)).contains("ORDER_HAS_ACTIVE_REQUEST");
    }

    @Test
    @DisplayName("validateForSubmit ignores the current draft when checking for active duplicates")
    void validateForSubmit_currentDraftIsIgnoredInActiveCheck() {
        when(snowflakeReader.validateOrderForBuyer(anyString(), anyString())).thenReturn(true);
        CreditRequest self = baseRequest();
        when(creditRequestRepository.findActiveByOrderAndBuyer(
                eq(ORDER_NUMBER), eq(BUYER_CODE_ID), eq(SystemStatus.DECLINED)))
                .thenReturn(List.of(self));
        self.setHasMissingDevice(true);
        self.setShipmentDamaged(ShipmentDamaged.NO);
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(REQUEST_ID))
                .thenReturn(List.of(new MissingDeviceLine()));

        SubmissionValidationResult result = validator.validateForSubmit(self, BUYER_CODE);

        assertThat(codesOf(result)).doesNotContain("ORDER_HAS_ACTIVE_REQUEST");
    }

    // ─── Reason selection ──────────────────────────────────────────────

    @Test
    @DisplayName("validateForSubmit flags NO_REASON_SELECTED when all three reason flags are false")
    void validateForSubmit_noReasonsSelected_flagsNoReason() {
        primeReaderAndRepos();
        CreditRequest cr = baseRequest();
        cr.setShipmentDamaged(ShipmentDamaged.NO);

        SubmissionValidationResult result = validator.validateForSubmit(cr, BUYER_CODE);

        assertThat(codesOf(result)).contains("NO_REASON_SELECTED");
    }

    // ─── Per-reason line presence ─────────────────────────────────────

    @Test
    @DisplayName("validateForSubmit flags MISSING_DEVICE_LINES_REQUIRED when has_missing=true but no lines")
    void validateForSubmit_missingFlagButNoLines_flagsLinesRequired() {
        primeReaderAndRepos();
        CreditRequest cr = baseRequest();
        cr.setHasMissingDevice(true);
        cr.setShipmentDamaged(ShipmentDamaged.NO);
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(REQUEST_ID))
                .thenReturn(Collections.emptyList());

        SubmissionValidationResult result = validator.validateForSubmit(cr, BUYER_CODE);

        assertThat(codesOf(result)).contains("MISSING_DEVICE_LINES_REQUIRED");
    }

    @Test
    @DisplayName("validateForSubmit flags WRONG_DEVICE_LINES_REQUIRED when has_wrong=true but no lines")
    void validateForSubmit_wrongFlagButNoLines_flagsLinesRequired() {
        primeReaderAndRepos();
        CreditRequest cr = baseRequest();
        cr.setHasWrongDevice(true);
        cr.setShipmentDamaged(ShipmentDamaged.NO);
        when(wrongDeviceLineRepository.findByCreditRequestIdOrderById(REQUEST_ID))
                .thenReturn(Collections.emptyList());

        SubmissionValidationResult result = validator.validateForSubmit(cr, BUYER_CODE);

        assertThat(codesOf(result)).contains("WRONG_DEVICE_LINES_REQUIRED");
    }

    @Test
    @DisplayName("validateForSubmit flags ENCUMBERED_DEVICE_LINES_REQUIRED when has_encumbered=true but no lines")
    void validateForSubmit_encumberedFlagButNoLines_flagsLinesRequired() {
        primeReaderAndRepos();
        CreditRequest cr = baseRequest();
        cr.setHasEncumberedDevice(true);
        cr.setShipmentDamaged(ShipmentDamaged.NO);
        when(encumberedDeviceLineRepository.findByCreditRequestIdOrderById(REQUEST_ID))
                .thenReturn(Collections.emptyList());

        SubmissionValidationResult result = validator.validateForSubmit(cr, BUYER_CODE);

        assertThat(codesOf(result)).contains("ENCUMBERED_DEVICE_LINES_REQUIRED");
    }

    @Test
    @DisplayName("validateForSubmit accepts a multi-reason request when every flagged reason has lines")
    void validateForSubmit_multiReasonWithAllLines_isValid() {
        primeReaderAndRepos();
        CreditRequest cr = baseRequest();
        cr.setHasMissingDevice(true);
        cr.setHasWrongDevice(true);
        cr.setHasEncumberedDevice(true);
        cr.setShipmentDamaged(ShipmentDamaged.NO);
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(REQUEST_ID))
                .thenReturn(List.of(new MissingDeviceLine()));
        when(wrongDeviceLineRepository.findByCreditRequestIdOrderById(REQUEST_ID))
                .thenReturn(List.of(new WrongDeviceLine()));
        when(encumberedDeviceLineRepository.findByCreditRequestIdOrderById(REQUEST_ID))
                .thenReturn(List.of(new EncumberedDeviceLine()));

        SubmissionValidationResult result = validator.validateForSubmit(cr, BUYER_CODE);

        assertThat(result.valid()).isTrue();
    }

    // ─── Damage Q&A ────────────────────────────────────────────────────

    @Test
    @DisplayName("validateForSubmit flags DAMAGE_NOT_ANSWERED when shipment_damaged is NOT_ANSWERED")
    void validateForSubmit_damageNotAnswered_flagsNotAnswered() {
        primeReaderAndRepos();
        CreditRequest cr = baseRequest();
        cr.setHasMissingDevice(true);
        cr.setShipmentDamaged(ShipmentDamaged.NOT_ANSWERED);
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(REQUEST_ID))
                .thenReturn(List.of(new MissingDeviceLine()));

        SubmissionValidationResult result = validator.validateForSubmit(cr, BUYER_CODE);

        assertThat(codesOf(result)).contains("DAMAGE_NOT_ANSWERED");
    }

    @Test
    @DisplayName("validateForSubmit flags DAMAGE_REQUIRES_PHOTO when shipment_damaged=YES and no damage photo")
    void validateForSubmit_damageYesNoPhoto_flagsPhotoRequired() {
        primeReaderAndRepos();
        CreditRequest cr = baseRequest();
        cr.setHasMissingDevice(true);
        cr.setShipmentDamaged(ShipmentDamaged.YES);
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(REQUEST_ID))
                .thenReturn(List.of(new MissingDeviceLine()));
        when(photoRepository.countByCreditRequestIdAndKind(REQUEST_ID, PhotoKind.DAMAGE)).thenReturn(0L);

        SubmissionValidationResult result = validator.validateForSubmit(cr, BUYER_CODE);

        assertThat(codesOf(result)).contains("DAMAGE_REQUIRES_PHOTO");
    }

    @Test
    @DisplayName("validateForSubmit accepts shipment_damaged=YES when at least one damage photo exists")
    void validateForSubmit_damageYesWithPhoto_isValid() {
        primeReaderAndRepos();
        CreditRequest cr = baseRequest();
        cr.setHasMissingDevice(true);
        cr.setShipmentDamaged(ShipmentDamaged.YES);
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(REQUEST_ID))
                .thenReturn(List.of(new MissingDeviceLine()));
        when(photoRepository.countByCreditRequestIdAndKind(REQUEST_ID, PhotoKind.DAMAGE)).thenReturn(2L);

        SubmissionValidationResult result = validator.validateForSubmit(cr, BUYER_CODE);

        assertThat(result.valid()).isTrue();
    }

    // ─── validateForSubmitOrThrow ─────────────────────────────────────

    @Test
    @DisplayName("validateForSubmitOrThrow throws CreditRequestValidationException carrying issues")
    void validateForSubmitOrThrow_invalid_throws() {
        primeReaderAndRepos();
        CreditRequest cr = baseRequest();
        cr.setShipmentDamaged(ShipmentDamaged.NO);

        assertThatThrownBy(() -> validator.validateForSubmitOrThrow(cr, BUYER_CODE))
                .isInstanceOf(CreditRequestValidationException.class)
                .satisfies(ex -> {
                    CreditRequestValidationException e = (CreditRequestValidationException) ex;
                    assertThat(e.getIssues()).extracting(ValidationIssue::code)
                            .contains("NO_REASON_SELECTED");
                });
    }

    // ─── reconcileBarcodes ─────────────────────────────────────────────

    @Test
    @DisplayName("reconcileBarcodes deduplicates within batch and reports duplicate count")
    void reconcileBarcodes_dedupesWithinBatch() {
        when(snowflakeReader.getOrderLines(ORDER_NUMBER, BUYER_CODE))
                .thenReturn(List.of(
                        manifestLine("BC-1"),
                        manifestLine("BC-2")));

        BarcodeReconciliationResult result = validator.reconcileBarcodes(
                ORDER_NUMBER, BUYER_CODE, List.of("BC-1", "BC-1", "BC-2"));

        assertThat(result.validLines()).extracting(ManifestLine::barcode)
                .containsExactly("BC-1", "BC-2");
        assertThat(result.duplicates()).containsExactly("BC-1");
        assertThat(result.notInOrder()).isEmpty();
        assertThat(result.banner()).contains("1 duplicate").contains("0 not in order");
    }

    @Test
    @DisplayName("reconcileBarcodes flags barcodes that are not on the manifest")
    void reconcileBarcodes_classifiesNotInOrder() {
        when(snowflakeReader.getOrderLines(ORDER_NUMBER, BUYER_CODE))
                .thenReturn(List.of(manifestLine("BC-1")));

        BarcodeReconciliationResult result = validator.reconcileBarcodes(
                ORDER_NUMBER, BUYER_CODE, List.of("BC-1", "BC-99", "BC-100"));

        assertThat(result.validLines()).extracting(ManifestLine::barcode).containsExactly("BC-1");
        assertThat(result.notInOrder()).containsExactly("BC-99", "BC-100");
        assertThat(result.banner()).contains("0 duplicates").contains("2 not in order");
    }

    @Test
    @DisplayName("reconcileBarcodes returns an empty banner when every barcode is accepted")
    void reconcileBarcodes_allValid_emptyBanner() {
        when(snowflakeReader.getOrderLines(ORDER_NUMBER, BUYER_CODE))
                .thenReturn(List.of(manifestLine("BC-1"), manifestLine("BC-2")));

        BarcodeReconciliationResult result = validator.reconcileBarcodes(
                ORDER_NUMBER, BUYER_CODE, List.of("BC-1", "BC-2"));

        assertThat(result.hasNonValid()).isFalse();
        assertThat(result.banner()).isEmpty();
    }

    @Test
    @DisplayName("reconcileBarcodes trims whitespace and skips blanks")
    void reconcileBarcodes_normalisesInput() {
        when(snowflakeReader.getOrderLines(ORDER_NUMBER, BUYER_CODE))
                .thenReturn(List.of(manifestLine("BC-1")));

        BarcodeReconciliationResult result = validator.reconcileBarcodes(
                ORDER_NUMBER, BUYER_CODE, java.util.Arrays.asList("  BC-1  ", "", "   ", null));

        assertThat(result.validLines()).extracting(ManifestLine::barcode).containsExactly("BC-1");
        assertThat(result.duplicates()).isEmpty();
        assertThat(result.notInOrder()).isEmpty();
    }

    @Test
    @DisplayName("reconcileBarcodes handles an empty input list cleanly")
    void reconcileBarcodes_emptyInput_returnsEmptyBuckets() {
        BarcodeReconciliationResult result = validator.reconcileBarcodes(
                ORDER_NUMBER, BUYER_CODE, List.of());

        assertThat(result.validLines()).isEmpty();
        assertThat(result.duplicates()).isEmpty();
        assertThat(result.notInOrder()).isEmpty();
        assertThat(result.banner()).isEmpty();
    }

    @Test
    @DisplayName("reconcileBarcodes singular phrasing for one duplicate one not-in-order")
    void reconcileBarcodes_singularBanner() {
        when(snowflakeReader.getOrderLines(ORDER_NUMBER, BUYER_CODE))
                .thenReturn(List.of(manifestLine("BC-1")));

        BarcodeReconciliationResult result = validator.reconcileBarcodes(
                ORDER_NUMBER, BUYER_CODE, List.of("BC-1", "BC-1", "BC-X"));

        assertThat(result.banner()).isEqualTo("Removed 1 duplicate and 1 not in order.");
    }

    // ─── helpers ───────────────────────────────────────────────────────

    private CreditRequest baseRequest() {
        CreditRequest cr = new CreditRequest();
        cr.setId(REQUEST_ID);
        cr.setOrderNumber(ORDER_NUMBER);
        cr.setBuyerCodeId(BUYER_CODE_ID);
        cr.setOrderShippedDate(Instant.now().minus(5, ChronoUnit.DAYS));
        cr.setHasMissingDevice(false);
        cr.setHasWrongDevice(false);
        cr.setHasEncumberedDevice(false);
        return cr;
    }

    /** Wires the snowflake reader + repo defaults that represent the happy path. */
    private void primeReaderAndRepos() {
        when(snowflakeReader.validateOrderForBuyer(anyString(), anyString())).thenReturn(true);
        when(creditRequestRepository.findActiveByOrderAndBuyer(anyString(), anyLong(), any(SystemStatus.class)))
                .thenReturn(Collections.emptyList());
    }

    private static List<String> codesOf(SubmissionValidationResult result) {
        return result.issues().stream().map(ValidationIssue::code).toList();
    }

    private static ManifestLine manifestLine(String barcode) {
        return new ManifestLine(
                barcode, "IMEI-" + barcode, "Apple", "iPhone 12", "A_YYY",
                "EC-" + barcode, "BOX-1", new BigDecimal("100.00"), "TRK-1");
    }
}
