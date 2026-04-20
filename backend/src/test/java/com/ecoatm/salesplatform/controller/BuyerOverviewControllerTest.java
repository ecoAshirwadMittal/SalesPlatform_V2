package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.BuyerCodeDetail;
import com.ecoatm.salesplatform.dto.BuyerDetailResponse;
import com.ecoatm.salesplatform.dto.BuyerOverviewPageResponse;
import com.ecoatm.salesplatform.dto.BuyerOverviewResponse;
import com.ecoatm.salesplatform.dto.BuyerPermissions;
import com.ecoatm.salesplatform.dto.SalesRepSummary;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerStatus;
import com.ecoatm.salesplatform.repository.SalesRepresentativeRepository;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.BuyerEditService;
import com.ecoatm.salesplatform.service.BuyerOverviewService;
import org.springframework.context.ApplicationEventPublisher;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BuyerOverviewController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class BuyerOverviewControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private BuyerOverviewService buyerOverviewService;
    @MockBean private BuyerEditService buyerEditService;
    @MockBean private SalesRepresentativeRepository salesRepRepository;

    private String adminToken() {
        return jwtService.generateToken(1L, "admin@test.com", List.of("Administrator"), false);
    }

    private String complianceToken() {
        return jwtService.generateToken(2L, "compliance@test.com", List.of("Compliance"), false);
    }

    private String salesRepToken() {
        return jwtService.generateToken(3L, "sales@test.com", List.of("SalesRep"), false);
    }

    @Test
    void list_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/admin/buyers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void list_withUnauthorizedRole_returns403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/buyers")
                        .header("Authorization", "Bearer " + salesRepToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void list_withAdministrator_returns200() throws Exception {
        BuyerOverviewPageResponse page = new BuyerOverviewPageResponse(
                List.of(new BuyerOverviewResponse(1L, "Acme Corp", "AC001", "Active")),
                0, 20, 1, 1);
        when(buyerOverviewService.search(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/buyers")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].companyName").value("Acme Corp"))
                .andExpect(jsonPath("$.content[0].buyerCodesDisplay").value("AC001"))
                .andExpect(jsonPath("$.content[0].status").value("Active"));
    }

    @Test
    void list_withCompliance_returns200() throws Exception {
        BuyerOverviewPageResponse page = new BuyerOverviewPageResponse(
                List.of(), 0, 20, 0, 0);
        when(buyerOverviewService.search(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/buyers")
                        .header("Authorization", "Bearer " + complianceToken()))
                .andExpect(status().isOk());
    }

    @Test
    void getDetail_withAdmin_returns200() throws Exception {
        BuyerDetailResponse detail = new BuyerDetailResponse(
                1L, "Acme Corp", BuyerStatus.Active, false,
                List.of(new SalesRepSummary(10L, "John", "Doe")),
                List.of(new BuyerCodeDetail(100L, "AC001", "Wholesale", 5000, false, true)),
                BuyerPermissions.forAdmin());

        when(buyerEditService.get(eq(1L), any())).thenReturn(detail);

        mockMvc.perform(get("/api/v1/admin/buyers/1")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Acme Corp"))
                .andExpect(jsonPath("$.permissions.canEditSalesRep").value(true))
                .andExpect(jsonPath("$.salesReps[0].firstName").value("John"))
                .andExpect(jsonPath("$.buyerCodes[0].code").value("AC001"));
    }

    @Test
    void getDetail_notFound_returns404() throws Exception {
        when(buyerEditService.get(eq(999L), any()))
                .thenThrow(new EntityNotFoundException("Buyer not found: id=999"));

        mockMvc.perform(get("/api/v1/admin/buyers/999")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Buyer not found: id=999"));
    }

    @Test
    void getDetail_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/admin/buyers/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getDetail_withUnauthorizedRole_returns403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/buyers/1")
                        .header("Authorization", "Bearer " + salesRepToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void update_withCompliance_returns200() throws Exception {
        BuyerDetailResponse detail = new BuyerDetailResponse(
                1L, "Acme Updated", BuyerStatus.Active, false,
                List.of(), List.of(), BuyerPermissions.forCompliance());

        when(buyerEditService.update(eq(1L), any(), any())).thenReturn(detail);

        mockMvc.perform(put("/api/v1/admin/buyers/1")
                        .header("Authorization", "Bearer " + complianceToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"companyName":"Acme Updated","isSpecialBuyer":false}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Acme Updated"));
    }

    @Test
    void update_disableConflict_returns409() throws Exception {
        when(buyerEditService.update(eq(1L), any(), any()))
                .thenThrow(new BuyerEditService.BuyerDisableException("Cannot disable buyer: 3 active user(s)"));

        mockMvc.perform(put("/api/v1/admin/buyers/1")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"companyName":"Acme","status":"Disabled","isSpecialBuyer":false}
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Cannot disable buyer: 3 active user(s)"));
    }

    @Test
    void update_withSalesRep_returns403() throws Exception {
        mockMvc.perform(put("/api/v1/admin/buyers/1")
                        .header("Authorization", "Bearer " + salesRepToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"companyName":"Acme","isSpecialBuyer":false}
                        """))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_withAdmin_returns201() throws Exception {
        BuyerDetailResponse detail = new BuyerDetailResponse(
                99L, "NewCo", BuyerStatus.Active, false,
                List.of(), List.of(), BuyerPermissions.forAdmin());

        when(buyerEditService.create(any(), any())).thenReturn(detail);

        mockMvc.perform(post("/api/v1/admin/buyers")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"companyName":"NewCo","isSpecialBuyer":false}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.companyName").value("NewCo"))
                .andExpect(jsonPath("$.status").value("Active"));
    }

    @Test
    void create_withCompliance_returns403() throws Exception {
        mockMvc.perform(post("/api/v1/admin/buyers")
                        .header("Authorization", "Bearer " + complianceToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"companyName":"NewCo","isSpecialBuyer":false}
                        """))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_withoutCompanyName_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/admin/buyers")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"companyName":"","isSpecialBuyer":false}
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void toggleStatus_withAdmin_returns200() throws Exception {
        BuyerDetailResponse detail = new BuyerDetailResponse(
                1L, "Acme", BuyerStatus.Disabled, false,
                List.of(), List.of(), BuyerPermissions.forAdmin());

        when(buyerEditService.toggleStatus(eq(1L), any())).thenReturn(detail);

        mockMvc.perform(patch("/api/v1/admin/buyers/1/status")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Disabled"));
    }

    @Test
    void toggleStatus_withCompliance_returns403() throws Exception {
        mockMvc.perform(patch("/api/v1/admin/buyers/1/status")
                        .header("Authorization", "Bearer " + complianceToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void toggleStatus_disableConflict_returns409() throws Exception {
        when(buyerEditService.toggleStatus(eq(1L), any()))
                .thenThrow(new BuyerEditService.BuyerDisableException("Cannot disable buyer: 2 active user(s)"));

        mockMvc.perform(patch("/api/v1/admin/buyers/1/status")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Cannot disable buyer: 2 active user(s)"));
    }

    @Test
    void snowflakeSync_withAdmin_returns202() throws Exception {
        mockMvc.perform(post("/api/v1/admin/buyers/snowflake-sync")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Snowflake sync initiated"));
    }

    @Test
    void snowflakeSync_withCompliance_returns403() throws Exception {
        mockMvc.perform(post("/api/v1/admin/buyers/snowflake-sync")
                        .header("Authorization", "Bearer " + complianceToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void snowflakeSync_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/admin/buyers/snowflake-sync"))
                .andExpect(status().isUnauthorized());
    }
}
