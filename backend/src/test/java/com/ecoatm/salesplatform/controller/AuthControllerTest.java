package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.BuyerCodeResponse;
import com.ecoatm.salesplatform.dto.LoginResponse;
import com.ecoatm.salesplatform.security.JwtAuthenticationFilter;
import com.ecoatm.salesplatform.security.JwtService;
import com.ecoatm.salesplatform.security.SecurityConfig;
import com.ecoatm.salesplatform.service.AuthService;
import com.ecoatm.salesplatform.service.BuyerCodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.Cookie;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!",
    "app.jwt.expiration-ms=3600000",
    "app.jwt.remember-me-expiration-ms=7200000",
    "auth.cookie.secure=true"
})
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;

    @MockBean private AuthService authService;
    @MockBean private BuyerCodeService buyerCodeService;

    @Test
    void login_withValidCredentials_setsHttpOnlyCookieAndOmitsTokenFromBody() throws Exception {
        LoginResponse successResponse = new LoginResponse(true, "Authentication successful", "jwt-token-here");
        successResponse.setUser(new LoginResponse.UserInfo(1L, "John", "Doe", "John Doe", "john@test.com", "JD", null));
        when(authService.authenticateLocalUser(any())).thenReturn(successResponse);

        var result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"john@test.com\",\"password\":\"Password123!\",\"rememberMe\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.user.userId").value(1))
                .andReturn();

        String setCookie = result.getResponse().getHeader("Set-Cookie");
        assertThat(setCookie).isNotNull();
        assertThat(setCookie).contains("auth_token=jwt-token-here");
        assertThat(setCookie).contains("HttpOnly");
        assertThat(setCookie).contains("Secure");
        assertThat(setCookie).contains("SameSite=Strict");
        assertThat(setCookie).contains("Path=/");
        // Default TTL is 8h for non-rememberMe logins
        assertThat(setCookie).contains("Max-Age=28800");
    }

    @Test
    void login_withRememberMe_setsLongerMaxAge() throws Exception {
        LoginResponse successResponse = new LoginResponse(true, "ok", "jwt");
        successResponse.setUser(new LoginResponse.UserInfo(1L, "a", "b", "a b", "a@b.c", "AB", null));
        when(authService.authenticateLocalUser(any())).thenReturn(successResponse);

        var result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@b.c\",\"password\":\"P\",\"rememberMe\":true}"))
                .andExpect(status().isOk())
                .andReturn();

        // 24h = 86400s
        assertThat(result.getResponse().getHeader("Set-Cookie")).contains("Max-Age=86400");
    }

    @Test
    void logout_clearsCookie() throws Exception {
        var result = mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isNoContent())
                .andReturn();

        String setCookie = result.getResponse().getHeader("Set-Cookie");
        assertThat(setCookie).isNotNull();
        assertThat(setCookie).contains("auth_token=");
        assertThat(setCookie).contains("Max-Age=0");
        assertThat(setCookie).contains("HttpOnly");
    }

    @Test
    void me_withCookieToken_returnsUserInfo() throws Exception {
        String token = jwtService.generateToken(42L, "test@test.com", List.of("Bidder"), false);
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(42L, "Test", "User", "Test User", "test@test.com", "TU", null);
        when(authService.getCurrentUser(42L)).thenReturn(userInfo);

        mockMvc.perform(get("/api/v1/auth/me").cookie(new Cookie("auth_token", token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(42));
    }

    @Test
    void login_withInvalidCredentials_returns401() throws Exception {
        LoginResponse failResponse = new LoginResponse(false, "Incorrect Password", null);
        when(authService.authenticateLocalUser(any())).thenReturn(failResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"john@test.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void login_withValidCredentials_returnsUserInfoWithInitials() throws Exception {
        LoginResponse successResponse = new LoginResponse(true, "Authentication successful", "jwt-token");
        successResponse.setUser(new LoginResponse.UserInfo(9001L, "Admin", "User", "Admin User", "admin@test.com", "AU", null));
        when(authService.authenticateLocalUser(any())).thenReturn(successResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@test.com\",\"password\":\"Admin123!\",\"rememberMe\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.user.firstName").value("Admin"))
                .andExpect(jsonPath("$.user.lastName").value("User"))
                .andExpect(jsonPath("$.user.initials").value("AU"))
                .andExpect(jsonPath("$.user.email").value("admin@test.com"));
    }

    @Test
    void login_withBlankEmail_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\",\"password\":\"Password123!\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withBlankPassword_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"john@test.com\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withMissingBody_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void me_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_withValidToken_returnsUserInfo() throws Exception {
        String token = jwtService.generateToken(42L, "test@test.com", List.of("Bidder"), false);
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(42L, "Test", "User", "Test User", "test@test.com", "TU", null);
        when(authService.getCurrentUser(42L)).thenReturn(userInfo);

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(42))
                .andExpect(jsonPath("$.initials").value("TU"));
    }

    @Test
    void buyerCodes_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/buyer-codes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void buyerCodes_withValidToken_returnsData() throws Exception {
        String token = jwtService.generateToken(42L, "test@test.com", List.of("Bidder"), false);
        when(buyerCodeService.getBuyerCodesForUser(42L)).thenReturn(List.of(
                new BuyerCodeResponse(1L, "BC001", "Test Buyer", "Wholesale")
        ));

        mockMvc.perform(get("/api/v1/auth/buyer-codes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("BC001"))
                .andExpect(jsonPath("$[0].codeType").value("AUCTION"));
    }

    @Test
    void buyerCodes_premiumWholesaleCode_returnsPwsCodeType() throws Exception {
        String token = jwtService.generateToken(42L, "test@test.com", List.of("Bidder"), false);
        when(buyerCodeService.getBuyerCodesForUser(42L)).thenReturn(List.of(
                new BuyerCodeResponse(2L, "NB_PWS", "PWS Buyer", "Premium_Wholesale")
        ));

        mockMvc.perform(get("/api/v1/auth/buyer-codes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codeType").value("PWS"));
    }

    @Test
    void buyerCodes_wholesaleCode_returnsAuctionCodeType() throws Exception {
        String token = jwtService.generateToken(42L, "test@test.com", List.of("Bidder"), false);
        when(buyerCodeService.getBuyerCodesForUser(42L)).thenReturn(List.of(
                new BuyerCodeResponse(3L, "DDWS", "CHS Technology", "Wholesale")
        ));

        mockMvc.perform(get("/api/v1/auth/buyer-codes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codeType").value("AUCTION"));
    }

    @Test
    void buyerCodes_mixedCodes_returnsBothCodeTypes() throws Exception {
        String token = jwtService.generateToken(42L, "test@test.com", List.of("Bidder"), false);
        when(buyerCodeService.getBuyerCodesForUser(42L)).thenReturn(List.of(
                new BuyerCodeResponse(1L, "DDWS", "CHS Technology", "Wholesale"),
                new BuyerCodeResponse(2L, "NB_PWS", "PWS Buyer", "Premium_Wholesale")
        ));

        mockMvc.perform(get("/api/v1/auth/buyer-codes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codeType").value("AUCTION"))
                .andExpect(jsonPath("$[1].codeType").value("PWS"));
    }

    @Test
    void me_withExpiredToken_returns401() throws Exception {
        JwtService expiredJwtService = new JwtService(
                "test-secret-key-must-be-at-least-32-bytes-long-for-hmac!!", 0, 0);
        String token = expiredJwtService.generateToken(1L, "test@test.com", List.of("Bidder"), false);

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_withDummyToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer dummy-jwt-token-42"))
                .andExpect(status().isUnauthorized());
    }
}
