package com.ecoatm.salesplatform.dto.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

/**
 * POST body for {@code /templates/{id}/send-test}. {@code toAddress} is the
 * single recipient override sent to {@code EmailService.sendTemplated} in
 * place of the template's own {@code to_default} — validated non-blank and
 * shaped like an address before the (rate-limited, real-outbound-mail)
 * endpoint ever calls the service.
 */
public record SendTestRequest(
        @NotBlank @Email String toAddress,
        Map<String, Object> vars) {
}
