package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.BuyerCodeResponse;
import com.ecoatm.salesplatform.dto.LoginRequest;
import com.ecoatm.salesplatform.dto.LoginResponse;
import com.ecoatm.salesplatform.service.AuthService;
import com.ecoatm.salesplatform.service.BuyerCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    public static final String AUTH_COOKIE_NAME = "auth_token";
    private static final Duration DEFAULT_TTL = Duration.ofHours(8);
    private static final Duration REMEMBER_ME_TTL = Duration.ofHours(24);

    private final AuthService authService;
    private final BuyerCodeService buyerCodeService;

    @Value("${auth.cookie.secure:true}")
    private boolean cookieSecure;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginExternalUser(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.authenticateLocalUser(request);

        if (!response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        ResponseCookie cookie = buildAuthCookie(
                response.getToken(),
                request.isRememberMe() ? REMEMBER_ME_TTL : DEFAULT_TTL);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie expired = buildAuthCookie("", Duration.ZERO);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, expired.toString())
                .build();
    }

    /**
     * Mendix equivalent: ACT_GetCurrentUser microflow.
     * Returns current user info (name, initials) for SNP_UserInfoDisplay.
     * userId is extracted from the JWT token via @AuthenticationPrincipal.
     */
    @GetMapping("/me")
    public ResponseEntity<LoginResponse.UserInfo> getCurrentUser(
            @org.springframework.security.core.annotation.AuthenticationPrincipal Long userId) {
        LoginResponse.UserInfo user = authService.getCurrentUser(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Mendix equivalent: DS_PWS_BuyerCodes microflow.
     * Returns buyer codes accessible to the given user based on their role.
     * userId is extracted from the JWT token via @AuthenticationPrincipal.
     */
    @GetMapping("/buyer-codes")
    public ResponseEntity<List<BuyerCodeResponse>> getBuyerCodes(
            @org.springframework.security.core.annotation.AuthenticationPrincipal Long userId) {
        List<BuyerCodeResponse> codes = buyerCodeService.getBuyerCodesForUser(userId);
        return ResponseEntity.ok(codes);
    }

    @GetMapping("/sso")
    public ResponseEntity<?> handleSSORedirect(@RequestParam String target) {
        // Stub for ACT_Login_InternalUser / OpenURL functionality.
        // This will be replaced by standard Spring Security OAuth2 / SAML endpoints.
        // TODO(Theme 3): when the real SSO callback lands, it MUST issue the JWT
        // via buildAuthCookie(token, DEFAULT_TTL) and attach it as a Set-Cookie
        // header — matching the /login flow. Do NOT return the token in the
        // redirect URL, query string, or response body.
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "https://login.microsoftonline.com/ecoatm-sso-stub")
                .build();
    }

    /**
     * Builds the HttpOnly auth cookie used by /login, /logout, and (when
     * implemented) the SSO callback. Any new authenticated entrypoint must
     * reuse this helper so cookie attributes stay consistent — changing
     * SameSite/Secure/Path in one place is a cross-cutting security setting.
     */
    private ResponseCookie buildAuthCookie(String value, Duration maxAge) {
        return ResponseCookie.from(AUTH_COOKIE_NAME, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}
