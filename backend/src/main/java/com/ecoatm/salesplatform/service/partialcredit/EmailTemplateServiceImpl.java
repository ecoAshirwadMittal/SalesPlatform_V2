package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.dto.partialcredit.EmailTemplateUpdate;
import com.ecoatm.salesplatform.model.partialcredit.EmailTemplate;
import com.ecoatm.salesplatform.repository.partialcredit.EmailTemplateRepository;
import com.ecoatm.salesplatform.service.email.TemplateRenderer;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final EmailTemplateRepository repository;

    /** Shared {@code {{var}}}/{@code {{!var}}} substitution engine — see
     *  {@link TemplateRenderer} for the (byte-identical) rendering rules
     *  this class used to implement inline. */
    private final TemplateRenderer templateRenderer;

    /** In-process cache. Tiny (≤ 10 keys), single point of write
     *  (update), so a ConcurrentHashMap with explicit invalidation is
     *  enough — no need to pull in Spring Cache infrastructure for 3
     *  rows. */
    private final ConcurrentMap<String, EmailTemplate> cache = new ConcurrentHashMap<>();

    public EmailTemplateServiceImpl(EmailTemplateRepository repository, TemplateRenderer templateRenderer) {
        this.repository = repository;
        this.templateRenderer = templateRenderer;
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
        String subject = templateRenderer.renderPlain(template.getSubject(), vars);
        String bodyHtml = templateRenderer.render(template.getBodyHtml(), vars);
        String bodyText = template.getBodyText() == null
                ? null
                : templateRenderer.renderPlain(template.getBodyText(), vars);
        return new RenderedEmail(subject, bodyHtml, bodyText);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailTemplate> listAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailTemplate> findById(Long id) {
        return repository.findById(id);
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
                    templateRenderer.renderPlain(template.getSubject(), vars),
                    templateRenderer.render(template.getBodyHtml(), vars),
                    template.getBodyText() == null
                            ? null
                            : templateRenderer.renderPlain(template.getBodyText(), vars));
        });
    }

    private EmailTemplate ensureEnabled(EmailTemplate template) {
        if (!Boolean.TRUE.equals(template.getEnabled())) {
            throw new IllegalStateException(
                    "Email template is disabled: " + template.getTemplateKey());
        }
        return template;
    }
}
