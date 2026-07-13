package com.ecoatm.salesplatform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body for {@code POST /api/v1/auth/activate}.
 *
 * <p>Both fields are only checked for presence here ({@code @NotBlank}); the full
 * password policy (length + uppercase + special character, ported from the legacy
 * {@code ACT_CheckPasswordRequirements_activation} microflow) is enforced in
 * {@code AccountActivationService} so there is a single authoritative policy
 * message. The target user is derived from {@code token}, never from any
 * identity field in this body.
 */
@Getter
@Setter
public class ActivateAccountRequest {

    @NotBlank(message = "Activation token is required")
    private String token;

    @NotBlank(message = "Password is required")
    private String password;
}
