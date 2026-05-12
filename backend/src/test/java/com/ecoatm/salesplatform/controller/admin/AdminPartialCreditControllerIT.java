package com.ecoatm.salesplatform.controller.admin;

import com.ecoatm.salesplatform.model.partialcredit.CreditRequest;
import com.ecoatm.salesplatform.model.partialcredit.CreditRequestStatus;
import com.ecoatm.salesplatform.model.partialcredit.MissingDeviceLine;
import com.ecoatm.salesplatform.model.partialcredit.enums.ReviewDecision;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import com.ecoatm.salesplatform.repository.BuyerCodeRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestRepository;
import com.ecoatm.salesplatform.repository.partialcredit.CreditRequestStatusRepository;
import com.ecoatm.salesplatform.repository.partialcredit.EncumberedDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.MissingDeviceLineRepository;
import com.ecoatm.salesplatform.repository.partialcredit.WrongDeviceLineRepository;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.AdminStatusCounters;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.CompleteReviewResult;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.EncumberedLineEntry;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.GlobalDecisionResult;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.LineDecisionResult;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.LineKind;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.OpenReviewResult;
import com.ecoatm.salesplatform.service.partialcredit.AdminCreditRequestService.SectionDecisionResult;
import com.ecoatm.salesplatform.service.partialcredit.CreditCalculationService.HeaderSummary;
import com.ecoatm.salesplatform.service.partialcredit.CreditRequestValidationException;
import com.ecoatm.salesplatform.service.partialcredit.ValidationIssue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminPartialCreditController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class AdminPartialCreditControllerIT {

    @Autowired MockMvc mvc;
    @MockBean AdminCreditRequestService adminService;
    @MockBean CreditRequestRepository creditRequestRepository;
    @MockBean CreditRequestStatusRepository statusRepository;
    @MockBean MissingDeviceLineRepository missingDeviceLineRepository;
    @MockBean WrongDeviceLineRepository wrongDeviceLineRepository;
    @MockBean EncumberedDeviceLineRepository encumberedDeviceLineRepository;
    @MockBean BuyerCodeRepository buyerCodeRepository;

    CreditRequestStatus underReviewRow;

    @BeforeEach
    void primeDefaults() {
        underReviewRow = new CreditRequestStatus();
        underReviewRow.setId(3L);
        underReviewRow.setSystemStatus(SystemStatus.UNDER_REVIEW);
        underReviewRow.setExternalStatusText("Pending Approval");
        underReviewRow.setColorHex("#F1C40F");

        when(statusRepository.findById(anyLong())).thenReturn(Optional.of(underReviewRow));
        when(statusRepository.findAll()).thenReturn(List.of(underReviewRow));
        when(missingDeviceLineRepository.findByCreditRequestIdOrderById(anyLong()))
                .thenReturn(List.of());
        when(wrongDeviceLineRepository.findByCreditRequestIdOrderById(anyLong()))
                .thenReturn(List.of());
        when(encumberedDeviceLineRepository.findByCreditRequestIdOrderById(anyLong()))
                .thenReturn(List.of());
    }

    // -------------------------------------------------------------------
    // GET /  (landing list + counters)
    // -------------------------------------------------------------------

    @Test
    void list_asSalesOps_returns200_withRowsAndCounters() throws Exception {
        CreditRequest cr = newRequest(100L, "PCR-1", "SO-1", underReviewRow.getId());
        Page<CreditRequest> page = new PageImpl<>(List.of(cr));
        when(adminService.listForAdmin(any(), any())).thenReturn(page);
        when(adminService.statusCounters()).thenReturn(
                new AdminStatusCounters(5, 2, 1, 1));

        mvc.perform(get("/api/v1/admin/partial-credit").with(salesOps()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.rows[0].id").value(100))
           .andExpect(jsonPath("$.rows[0].requestNumber").value("PCR-1"))
           .andExpect(jsonPath("$.rows[0].statusColorHex").value("#F1C40F"))
           .andExpect(jsonPath("$.counters.pendingApproval").value(5))
           .andExpect(jsonPath("$.counters.underReview").value(2))
           .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    void list_asBidder_returns403() throws Exception {
        mvc.perform(get("/api/v1/admin/partial-credit").with(bidder()))
           .andExpect(status().isForbidden());
    }

    // -------------------------------------------------------------------
    // POST /{id}/open-for-review
    // -------------------------------------------------------------------

    @Test
    void openForReview_asSalesOps_returns200() throws Exception {
        CreditRequest cr = newRequest(100L, "PCR-1", "SO-1", underReviewRow.getId());
        OpenReviewResult result = new OpenReviewResult(
                cr, List.of(), List.of(), List.of(), emptySummary(), true);
        when(adminService.openForReview(eq(100L), any())).thenReturn(result);

        mvc.perform(post("/api/v1/admin/partial-credit/100/open-for-review").with(salesOps()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(100))
           .andExpect(jsonPath("$.systemStatus").value("UNDER_REVIEW"));
    }

    @Test
    void openForReview_asBidder_returns403() throws Exception {
        mvc.perform(post("/api/v1/admin/partial-credit/100/open-for-review").with(bidder()))
           .andExpect(status().isForbidden());
    }

    // -------------------------------------------------------------------
    // GET /{id}  (read-only detail)
    // -------------------------------------------------------------------

    @Test
    void getById_returnsDetail() throws Exception {
        CreditRequest cr = newRequest(100L, "PCR-1", "SO-1", underReviewRow.getId());
        when(creditRequestRepository.findById(100L)).thenReturn(Optional.of(cr));

        mvc.perform(get("/api/v1/admin/partial-credit/100").with(salesOps()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(100))
           .andExpect(jsonPath("$.orderNumber").value("SO-1"));
    }

    // -------------------------------------------------------------------
    // POST /{id}/lines/{lineId}/decision
    // -------------------------------------------------------------------

    @Test
    void setLineDecision_happyPath_returnsLineProjection() throws Exception {
        MissingDeviceLine line = newMissingLine(700L, 100L);
        line.setReviewDecision(ReviewDecision.ACCEPTED);
        line.setAmountToCredit(new BigDecimal("50.00"));
        LineDecisionResult result = new LineDecisionResult(line,
                new HeaderSummary(0, 1, new BigDecimal("50.00"), 0, 1, new BigDecimal("50.00")));
        when(adminService.setLineDecision(eq(100L), eq(700L), eq(LineKind.MISSING),
                eq(ReviewDecision.ACCEPTED), any())).thenReturn(result);

        mvc.perform(post("/api/v1/admin/partial-credit/100/lines/700/decision")
                .with(salesOps())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"kind\":\"MISSING\",\"decision\":\"ACCEPTED\"}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.line.kind").value("MISSING"))
           .andExpect(jsonPath("$.line.missingLine.id").value(700))
           .andExpect(jsonPath("$.line.missingLine.reviewDecision").value("ACCEPTED"))
           .andExpect(jsonPath("$.summary.approvedTotal").value(50.00));
    }

    // -------------------------------------------------------------------
    // POST /{id}/sections/{kind}/decision
    // -------------------------------------------------------------------

    @Test
    void setSectionDecision_happyPath() throws Exception {
        SectionDecisionResult result = new SectionDecisionResult(
                LineKind.WRONG, 3,
                new HeaderSummary(0, 3, new BigDecimal("100.00"), 0, 3, new BigDecimal("70.00")));
        when(adminService.setSectionDecision(eq(100L), eq(LineKind.WRONG),
                eq(ReviewDecision.ACCEPTED), any())).thenReturn(result);

        mvc.perform(post("/api/v1/admin/partial-credit/100/sections/WRONG/decision")
                .with(salesOps())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"decision\":\"ACCEPTED\"}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.kind").value("WRONG"))
           .andExpect(jsonPath("$.updatedCount").value(3))
           .andExpect(jsonPath("$.summary.approvedTotal").value(70.00));
    }

    // -------------------------------------------------------------------
    // POST /{id}/decision  (global bulk)
    // -------------------------------------------------------------------

    @Test
    void setGlobalDecision_happyPath() throws Exception {
        GlobalDecisionResult result = new GlobalDecisionResult(
                2, 3, 1,
                new HeaderSummary(0, 6, new BigDecimal("200.00"), 0, 6, new BigDecimal("150.00")));
        when(adminService.setGlobalDecision(eq(100L), eq(ReviewDecision.ACCEPTED), any()))
                .thenReturn(result);

        mvc.perform(post("/api/v1/admin/partial-credit/100/decision")
                .with(salesOps())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"decision\":\"ACCEPTED\"}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.missingUpdated").value(2))
           .andExpect(jsonPath("$.wrongUpdated").value(3))
           .andExpect(jsonPath("$.encumberedUpdated").value(1));
    }

    // -------------------------------------------------------------------
    // POST /{id}/lines/{lineId}/encumbered
    // -------------------------------------------------------------------

    @Test
    void setEncumberedFields_happyPath() throws Exception {
        com.ecoatm.salesplatform.model.partialcredit.EncumberedDeviceLine line =
                new com.ecoatm.salesplatform.model.partialcredit.EncumberedDeviceLine();
        line.setId(900L);
        line.setCreditRequestId(100L);
        line.setBarcodeSubmitted("BC-X");
        line.setPrologResult(com.ecoatm.salesplatform.model.partialcredit.enums.PrologResult.ENCUMBERED);
        line.setActualValue(new BigDecimal("25.00"));
        line.setAmountToCredit(new BigDecimal("25.00"));
        EncumberedLineEntry result = new EncumberedLineEntry(line,
                new HeaderSummary(0, 1, new BigDecimal("40.00"), 0, 1, new BigDecimal("25.00")));
        when(adminService.setEncumberedFields(eq(100L), eq(900L),
                eq(com.ecoatm.salesplatform.model.partialcredit.enums.PrologResult.ENCUMBERED),
                eq(new BigDecimal("25.00")), any())).thenReturn(result);

        mvc.perform(post("/api/v1/admin/partial-credit/100/lines/900/encumbered")
                .with(salesOps())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"prologResult\":\"ENCUMBERED\",\"actualValue\":25.00}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.line.kind").value("ENCUMBERED"))
           .andExpect(jsonPath("$.line.encumberedLine.id").value(900))
           .andExpect(jsonPath("$.line.encumberedLine.prologResult").value("ENCUMBERED"))
           .andExpect(jsonPath("$.line.encumberedLine.actualValue").value(25.00))
           .andExpect(jsonPath("$.summary.approvedTotal").value(25.00));
    }

    // -------------------------------------------------------------------
    // POST /{id}/complete-review
    // -------------------------------------------------------------------

    @Test
    void completeReview_happyPath_returns200() throws Exception {
        CreditRequest cr = newRequest(100L, "PCR-1", "SO-1", underReviewRow.getId());
        cr.setReviewCompletedOn(Instant.parse("2026-05-11T12:00:00Z"));
        CompleteReviewResult result = new CompleteReviewResult(
                cr, SystemStatus.APPROVED,
                new HeaderSummary(0, 1, new BigDecimal("50.00"), 0, 1, new BigDecimal("50.00")),
                List.of(), List.of(), List.of());
        when(adminService.completeReview(eq(100L), eq(SystemStatus.APPROVED), any()))
                .thenReturn(result);

        mvc.perform(post("/api/v1/admin/partial-credit/100/complete-review")
                .with(salesOps())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"outcome\":\"APPROVED\"}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(100))
           .andExpect(jsonPath("$.outcome").value("APPROVED"))
           .andExpect(jsonPath("$.summary.approvedTotal").value(50.00));
    }

    @Test
    void completeReview_pendingLines_returns400_withIssue() throws Exception {
        when(adminService.completeReview(eq(100L), eq(SystemStatus.APPROVED), any()))
                .thenThrow(new CreditRequestValidationException(List.of(
                        new ValidationIssue("LINES_PENDING_DECISION",
                                "Every line must have a decision."))));

        mvc.perform(post("/api/v1/admin/partial-credit/100/complete-review")
                .with(salesOps())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"outcome\":\"APPROVED\"}"))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
           .andExpect(jsonPath("$.issues[0].code").value("LINES_PENDING_DECISION"));
    }

    // -------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------

    private static RequestPostProcessor salesOps() {
        return authentication(asAuth(2L, "ops@test.com", "SalesOps"));
    }

    private static RequestPostProcessor bidder() {
        return authentication(asAuth(1L, "bidder@test.com", "Bidder"));
    }

    private static UsernamePasswordAuthenticationToken asAuth(
            Long userId, String email, String role) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        return new UsernamePasswordAuthenticationToken(userId, email, authorities);
    }

    private static CreditRequest newRequest(Long id, String number, String order, Long statusId) {
        CreditRequest cr = new CreditRequest();
        cr.setId(id);
        cr.setRequestNumber(number);
        cr.setOrderNumber(order);
        cr.setBuyerCodeId(500L);
        cr.setStatusId(statusId);
        cr.setRequestDate(Instant.parse("2026-05-01T00:00:00Z"));
        cr.setHasMissingDevice(false);
        cr.setHasWrongDevice(false);
        cr.setHasEncumberedDevice(false);
        return cr;
    }

    private static MissingDeviceLine newMissingLine(Long id, Long crId) {
        MissingDeviceLine line = new MissingDeviceLine();
        line.setId(id);
        line.setCreditRequestId(crId);
        line.setBarcodeSubmitted("BC-" + id);
        line.setAmountPaid(new BigDecimal("50.00"));
        return line;
    }

    private static HeaderSummary emptySummary() {
        return new HeaderSummary(0, 0, BigDecimal.ZERO, 0, 0, BigDecimal.ZERO);
    }
}
