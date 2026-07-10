package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.BuyerCodeResponse;
import com.ecoatm.salesplatform.dto.ForgotPasswordRequest;
import com.ecoatm.salesplatform.dto.LoginRequest;
import com.ecoatm.salesplatform.dto.LoginResponse;
import com.ecoatm.salesplatform.dto.ResetPasswordRequest;
import com.ecoatm.salesplatform.service.AuthService;
import com.ecoatm.salesplatform.service.BuyerCodeService;
import com.ecoatm.salesplatform.security.AuthRateLimiter;
import com.ecoatm.salesplatform.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final PasswordResetService passwordResetService;
    private final AuthRateLimiter authRateLimiter;

    @Value("${auth.cookie.secure:true}")
    private boolean cookieSecure;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginExternalUser(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        if (!authRateLimiter.tryAcquire(clientKey(httpRequest))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
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

    /**
     * Enumeration-resistant password-reset request.
     *
     * <p>Always returns {@code 200 OK} regardless of whether the email maps to
     * an existing account. The caller should show the same generic message in
     * both cases: "If an account exists with that email, a reset link has been sent."
     *
     * <p>Email delivery is logged only in this phase.
     * TODO(email-infra): see PasswordResetService for the delivery deferral note.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest) {
        if (!authRateLimiter.tryAcquire(clientKey(httpRequest))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        passwordResetService.requestReset(request.getEmail());
        return ResponseEntity.ok().build();
    }

    /**
     * Validate a reset token and update the user's password.
     *
     * <p>Returns {@code 200 OK} on success; {@code 400 Bad Request} with a
     * generic "Invalid or expired token" message on any failure — never reveals
     * whether the token, user, or expiry was the cause.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest) {
        if (!authRateLimiter.tryAcquire(clientKey(httpRequest))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        passwordResetService.confirmReset(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sso")
    public ResponseEntity<?> handleSSORedirect(@RequestParam String target) {
        // Stub for ACT_Login_InternalUser / OpenURL functionality.
        // This will be replaced by standard Spring Security OAuth2 / SAML endpoints.
        //
        // L-5 (security review 2026-07-10): `target` is inert today — the redirect
        // below is a hardcoded stub and never uses it — but it is an open-redirect
        // magnet for the future SAML wiring. Guard it now so a later implementer
        // cannot forward the caller to an attacker-controlled absolute URL: only a
        // safe *internal* path may ever drive a redirect (leading "/", but NOT
        // "//host" or "/\host", which browsers resolve as protocol-relative →
        // external). We reject an unsafe value up front rather than silently
        // ignore it, so the constraint is enforced the moment `target` is wired in.
        // This does NOT change the redirect destination for a valid/absent target.
        if (target != null && !isSafeInternalRedirectTarget(target)) {
            return ResponseEntity.badRequest().build();
        }
        // TODO(Theme 3): when the real SSO callback lands, it MUST issue the JWT
        // via buildAuthCookie(token, DEFAULT_TTL) and attach it as a Set-Cookie
        // header — matching the /login flow. Do NOT return the token in the
        // redirect URL, query string, or response body. Redirect ONLY to a
        // validated internal `target` (see isSafeInternalRedirectTarget) — never
        // to a caller-supplied absolute URL.
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "https://login.microsoftonline.com/ecoatm-sso-stub")
                .build();
    }

    /**
     * True only for a safe <em>internal</em> redirect target: a path rooted at
     * {@code "/"} that is not protocol-relative ({@code "//host"}) or a backslash
     * variant ({@code "/\\host"}) — both of which browsers resolve to an external
     * origin. Keeps the future SSO {@code target} param from becoming an
     * open redirect (security review 2026-07-10, L-5).
     */
    private static boolean isSafeInternalRedirectTarget(String target) {
        return target.startsWith("/")
                && !target.startsWith("//")
                && !target.startsWith("/\\");
    }

    /**
     * Resolves the caller's IP for rate-limiting — prefers the first hop in
     * X-Forwarded-For (behind a proxy/CDN) and falls back to the socket address.
     */
    private static String clientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
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
