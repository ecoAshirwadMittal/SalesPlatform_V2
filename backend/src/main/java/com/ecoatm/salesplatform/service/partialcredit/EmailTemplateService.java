package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.dto.partialcredit.EmailTemplateUpdate;
import com.ecoatm.salesplatform.model.partialcredit.EmailTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Renders DB-backed email templates and exposes the admin CRUD surface
 * (used by Chunk 3's admin REST endpoints).
 *
 * <p>Templates use {@code {{varName}}} substitution. HTML rendering
 * escapes substituted values by default; raw HTML is opted-in per
 * variable via {@code {{!varName}}} so admins can embed pre-built
 * anchors / spans for trusted fields. Plain-text rendering never
 * escapes — both forms produce raw text.
 *
 * <p>Missing variables render as empty string and emit a warn log so
 * the gap is observable without breaking the send.
 */
public interface EmailTemplateService {

    /** Load a template by key. Caches in-process — invalidated on every
     *  {@link #update} call. Throws if the key is missing or the row is
     *  disabled. */
    EmailTemplate get(String templateKey);

    /** Render the template at {@code templateKey} against the variable
     *  map. The map values are coerced to {@code String} via
     *  {@link String#valueOf(Object)} unless they are themselves
     *  {@code String}; nulls render as empty string + warn log. */
    RenderedEmail render(String templateKey, Map<String, Object> variables);

    /** Admin: list every template (enabled and disabled). Backs the
     *  Chunk-3 admin landing. */
    List<EmailTemplate> listAll();

    /** Admin: load one template by id (enabled or disabled). Used by
     *  the Chunk-3 preview endpoint which takes a row id, not a key. */
    Optional<EmailTemplate> findById(Long id);

    /** Admin: patch one template. Bumps {@code changed_date} and
     *  {@code changed_by_id}. Invalidates the in-process cache. */
    EmailTemplate update(Long id, EmailTemplateUpdate patch, Long changedByUserId);

    /** Admin: render a template with the supplied variables for the
     *  Chunk-3 preview tab. Does not require the template to be
     *  enabled. Returns empty if the key is missing. */
    Optional<RenderedEmail> renderPreview(String templateKey, Map<String, Object> variables);

    record RenderedEmail(String subject, String bodyHtml, String bodyText) {}
}
