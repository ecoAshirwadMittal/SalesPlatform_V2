package com.ecoatm.salesplatform.dto.partialcredit;

import java.util.Map;

/**
 * POST body for the email-template preview endpoint. The admin editor
 * supplies the variable map the preview should render against; an empty
 * map renders the template verbatim (placeholders intact + warn logs in
 * the backend), which is fine for "what does this even look like" checks.
 */
public record EmailTemplatePreviewRequest(Map<String, Object> variables) {
}
