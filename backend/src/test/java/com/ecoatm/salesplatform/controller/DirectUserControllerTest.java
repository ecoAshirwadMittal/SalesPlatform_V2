package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.*;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.DirectUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DirectUserController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000"
})
class DirectUserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private DirectUserService directUserService;

    private String validToken() {
        return jwtService.generateToken(1L, "admin@test.com", List.of("Administrator"), false);
    }

    @Test
    void listDirectUsers_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/direct-users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listDirectUsers_withToken_returns200() throws Exception {
        DirectUserPageResponse page = new DirectUserPageResponse(
                Collections.emptyList(), 0, 20, 0, 0);
        when(directUserService.getDirectUsers(isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), eq(20)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/users/direct-users")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void getDirectUser_withToken_returns200() throws Exception {
        DirectUserDetailResponse detail = new DirectUserDetailResponse();
        detail.setUserId(1L);
        detail.setFirstName("John");
        when(directUserService.getDirectUserDetail(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/users/direct-users/1")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void createDirectUser_withToken_returns200() throws Exception {
        DirectUserDetailResponse detail = new DirectUserDetailResponse();
        detail.setUserId(42L);
        detail.setFirstName("New");
        when(directUserService.createDirectUser(any())).thenReturn(detail);

        mockMvc.perform(post("/api/v1/users/direct-users")
                        .header("Authorization", "Bearer " + validToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"New\",\"lastName\":\"User\",\"email\":\"new@test.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(42));
    }

    @Test
    void getAllRoles_withToken_returns200() throws Exception {
        when(directUserService.getAllRoles()).thenReturn(List.of(new RoleResponse(1L, "Admin")));

        mockMvc.perform(get("/api/v1/users/roles")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Admin"));
    }

    @Test
    void getAllBuyers_withToken_returns200() throws Exception {
        when(directUserService.getAllBuyers()).thenReturn(List.of(new BuyerResponse(1L, "Acme")));

        mockMvc.perform(get("/api/v1/users/buyers")
                        .header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].companyName").value("Acme"));
    }
}
