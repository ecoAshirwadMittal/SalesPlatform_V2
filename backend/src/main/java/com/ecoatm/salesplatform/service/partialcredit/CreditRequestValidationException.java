package com.ecoatm.salesplatform.service.partialcredit;

import java.util.List;

/**
 * Thrown by {@code CreditRequestValidator.validateForSubmitOrThrow(...)}
 * when at least one rule from
 * {@code partial-credit-implementation-plan.md} §7 fails. The controller
 * layer maps this to HTTP 400 and serialises {@link #issues()} into the
 * response body so the wizard can highlight the failing fields.
 */
public class CreditRequestValidationException extends RuntimeException {

    private final List<ValidationIssue> issues;

    public CreditRequestValidationException(List<ValidationIssue> issues) {
        super(buildMessage(issues));
        this.issues = List.copyOf(issues);
    }

    public List<ValidationIssue> getIssues() {
        return issues;
    }

    private static String buildMessage(List<ValidationIssue> issues) {
        if (issues.isEmpty()) {
            return "Credit request validation failed (no issues attached)";
        }
        StringBuilder sb = new StringBuilder("Credit request validation failed: ");
        for (int i = 0; i < issues.size(); i++) {
            if (i > 0) sb.append("; ");
            sb.append(issues.get(i).code());
        }
        return sb.toString();
    }
}
