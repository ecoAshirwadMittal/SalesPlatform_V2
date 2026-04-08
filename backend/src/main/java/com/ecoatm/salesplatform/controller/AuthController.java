package com.ecoatm.salesplatform.controller;

import com.ecoatm.salesplatform.dto.BuyerCodeResponse;
import com.ecoatm.salesplatform.dto.LoginRequest;
import com.ecoatm.salesplatform.dto.LoginResponse;
import com.ecoatm.salesplatform.service.AuthService;
import com.ecoatm.salesplatform.service.BuyerCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final BuyerCodeService buyerCodeService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginExternalUser(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.authenticateLocalUser(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
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
        // Stub for ACT_Login_InternalUser / OpenURL functionality
        // This will be replaced by standard Spring Security OAuth2 / SAML endpoints
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "https://login.microsoftonline.com/ecoatm-sso-stub")
                .build();
    }
}
