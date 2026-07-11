package com.ecoatm.salesplatform.dto.email;

import java.util.Map;

/**
 * POST body for {@code /templates/{id}/preview}. The admin editor supplies
 * the variable map to render against; a missing/empty map renders the
 * template verbatim (placeholders intact, plus {@code TemplateRenderer}'s
 * own warn logs for each unresolved {@code {{var}}}), which is fine for a
 * "what does the raw template look like" check.
 */
public record PreviewRequest(Map<String, Object> vars) {
}
