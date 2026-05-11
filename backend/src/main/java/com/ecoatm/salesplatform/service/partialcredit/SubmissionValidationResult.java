package com.ecoatm.salesplatform.service.partialcredit;

import java.util.List;

/**
 * Outcome of running the full submit-time rule set against a credit
 * request. {@code valid()} is {@code true} only when {@link #issues()} is
 * empty. The list ordering is stable: order-eligibility issues first, then
 * reason-selection, then per-reason data, then damage Q&A — same order the
 * wizard surfaces them in the UI.
 */
public record SubmissionValidationResult(List<ValidationIssue> issues) {

    public boolean valid() {
        return issues.isEmpty();
    }

    public static SubmissionValidationResult ok() {
        return new SubmissionValidationResult(List.of());
    }
}
