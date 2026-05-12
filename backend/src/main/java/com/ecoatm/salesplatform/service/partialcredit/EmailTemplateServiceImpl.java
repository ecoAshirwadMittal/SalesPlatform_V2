package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.dto.partialcredit.EmailTemplateUpdate;
import com.ecoatm.salesplatform.model.partialcredit.EmailTemplate;
import com.ecoatm.salesplatform.repository.partialcredit.EmailTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private static final Logger log = LoggerFactory.getLogger(EmailTemplateServiceImpl.class);

    /** Matches {@code {{name}}} or {@code {{!name}}}. Group 1 is the
     *  raw-escape marker (present iff the variable opts out of HTML
     *  escaping); group 2 is the variable name. */
    private static final Pattern VAR_PATTERN = Pattern.compile("\\{\\{(!)?([A-Za-z0-9_]+)\\}\\}");

    private final EmailTemplateRepository repository;

    /** In-process cache. Tiny (≤ 10 keys), single point of write
     *  (update), so a ConcurrentHashMap with explicit invalidation is
     *  enough — no need to pull in Spring Cache infrastructure for 3
     *  rows. */
    private final ConcurrentMap<String, EmailTemplate> cache = new ConcurrentHashMap<>();

    public EmailTemplateServiceImpl(EmailTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public EmailTemplate get(String templateKey) {
        EmailTemplate cached = cache.get(templateKey);
        if (cached != null) {
            return ensureEnabled(cached);
        }
        EmailTemplate loaded = repository.findByTemplateKey(templateKey)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Email template not found: " + templateKey));
        cache.put(templateKey, loaded);
        return ensureEnabled(loaded);
    }

    @Override
    @Transactional(readOnly = true)
    public RenderedEmail render(String templateKey, Map<String, Object> variables) {
        EmailTemplate template = get(templateKey);
        Map<String, Object> vars = variables == null ? Map.of() : variables;
        String subject = substitute(template.getSubject(), vars, false);
        String bodyHtml = substitute(template.getBodyHtml(), vars, true);
        String bodyText = template.getBodyText() == null
                ? null
                : substitute(template.getBodyText(), vars, false);
        return new RenderedEmail(subject, bodyHtml, bodyText);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailTemplate> listAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public EmailTemplate update(Long id, EmailTemplateUpdate patch, Long changedByUserId) {
        EmailTemplate template = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Email template not found: id=" + id));
        if (patch.subject() != null) template.setSubject(patch.subject());
        if (patch.bodyHtml() != null) template.setBodyHtml(patch.bodyHtml());
        if (patch.bodyText() != null) template.setBodyText(patch.bodyText());
        if (patch.enabled() != null) template.setEnabled(patch.enabled());
        if (patch.description() != null) template.setDescription(patch.description());
        template.setChangedDate(Instant.now());
        template.setChangedById(changedByUserId);
        EmailTemplate saved = repository.save(template);
        cache.clear();
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RenderedEmail> renderPreview(String templateKey, Map<String, Object> variables) {
        // Bypasses the enabled-check + cache: preview must work even when
        // an admin has just disabled the template to test before flipping
        // it back on.
        return repository.findByTemplateKey(templateKey).map(template -> {
            Map<String, Object> vars = variables == null ? Map.of() : variables;
            return new RenderedEmail(
                    substitute(template.getSubject(), vars, false),
                    substitute(template.getBodyHtml(), vars, true),
                    template.getBodyText() == null
                            ? null
                            : substitute(template.getBodyText(), vars, false));
        });
    }

    /**
     * Apply {@code {{varName}}} substitution. {@code escapeHtml=true}
     * escapes the substituted value unless the placeholder uses the
     * {@code {{!varName}}} raw-escape form. Missing variables substitute
     * to empty string + emit a warn log so they're observable.
     */
    private String substitute(String template, Map<String, Object> variables, boolean escapeHtml) {
        if (template == null || template.isEmpty()) {
            return template;
        }
        Matcher matcher = VAR_PATTERN.matcher(template);
        StringBuilder out = new StringBuilder(template.length() + 64);
        while (matcher.find()) {
            boolean raw = matcher.group(1) != null;
            String name = matcher.group(2);
            Object rawValue = variables.get(name);
            String value;
            if (rawValue == null) {
                if (!variables.containsKey(name)) {
                    log.warn(
                            "Email template variable '{}' has no value supplied — rendering as empty string",
                            name);
                }
                value = "";
            } else {
                value = String.valueOf(rawValue);
            }
            if (escapeHtml && !raw) {
                value = escapeHtml(value);
            }
            matcher.appendReplacement(out, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(out);
        return out.toString();
    }

    private EmailTemplate ensureEnabled(EmailTemplate template) {
        if (!Boolean.TRUE.equals(template.getEnabled())) {
            throw new IllegalStateException(
                    "Email template is disabled: " + template.getTemplateKey());
        }
        return template;
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
