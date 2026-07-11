package com.ecoatm.salesplatform.service.partialcredit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.dto.partialcredit.EmailTemplateUpdate;
import com.ecoatm.salesplatform.model.partialcredit.EmailTemplate;
import com.ecoatm.salesplatform.repository.partialcredit.EmailTemplateRepository;
import com.ecoatm.salesplatform.service.email.TemplateRenderer;
import com.ecoatm.salesplatform.service.partialcredit.EmailTemplateService.RenderedEmail;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

/**
 * Unit tests for {@link EmailTemplateServiceImpl}.
 *
 * <p>Three areas exercised:
 * <ol>
 *   <li>Rendering rules — variable substitution, HTML escaping by
 *       default, raw opt-out via {@code {{!varName}}}, missing-variable
 *       warn-log + empty substitution.</li>
 *   <li>Cache behaviour — {@link EmailTemplateService#get} memoises the
 *       repository lookup; {@link EmailTemplateService#update} clears it.</li>
 *   <li>Disabled-template + missing-key error paths.</li>
 * </ol>
 */
@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class EmailTemplateServiceTest {

    @Mock private EmailTemplateRepository repository;

    private EmailTemplateServiceImpl service;

    @BeforeEach
    void setUp() {
        // Real TemplateRenderer (not mocked): the whole point of this
        // regression suite is proving the render behavior is unchanged
        // now that EmailTemplateServiceImpl delegates to it.
        service = new EmailTemplateServiceImpl(repository, new TemplateRenderer());
    }

    @Test
    @DisplayName("render — substitutes a single variable into subject + html + text")
    void render_substitutesSingleVariable() {
        EmailTemplate template = newTemplate(
                "TestKey",
                "Hello {{name}}",
                "<p>Hi {{name}}!</p>",
                "Hi {{name}}!",
                true);
        when(repository.findByTemplateKey("TestKey")).thenReturn(Optional.of(template));

        RenderedEmail rendered = service.render("TestKey", Map.of("name", "World"));

        assertThat(rendered.subject()).isEqualTo("Hello World");
        assertThat(rendered.bodyHtml()).isEqualTo("<p>Hi World!</p>");
        assertThat(rendered.bodyText()).isEqualTo("Hi World!");
    }

    @Test
    @DisplayName("render — HTML-escapes substituted values in html body by default")
    void render_htmlEscapesByDefault() {
        EmailTemplate template = newTemplate(
                "EscapeKey",
                "{{name}}",
                "<p>{{name}}</p>",
                "{{name}}",
                true);
        when(repository.findByTemplateKey("EscapeKey")).thenReturn(Optional.of(template));

        RenderedEmail rendered = service.render(
                "EscapeKey",
                Map.of("name", "<script>alert('x')</script>"));

        // HTML body escapes <, >, ', "
        assertThat(rendered.bodyHtml())
                .isEqualTo("<p>&lt;script&gt;alert(&#39;x&#39;)&lt;/script&gt;</p>");
        // Subject + text body do NOT escape — they're not HTML contexts.
        assertThat(rendered.subject()).isEqualTo("<script>alert('x')</script>");
        assertThat(rendered.bodyText()).isEqualTo("<script>alert('x')</script>");
    }

    @Test
    @DisplayName("render — {{!varName}} opts out of HTML escape (admin-trusted raw)")
    void render_rawEscapeMarkerBypassesEscape() {
        EmailTemplate template = newTemplate(
                "RawKey",
                "x",
                "<p>{{!button}}</p>",
                null,
                true);
        when(repository.findByTemplateKey("RawKey")).thenReturn(Optional.of(template));

        RenderedEmail rendered = service.render(
                "RawKey",
                Map.of("button", "<a href=\"/foo\">click me</a>"));

        // Raw form preserves the anchor markup as-is.
        assertThat(rendered.bodyHtml()).isEqualTo("<p><a href=\"/foo\">click me</a></p>");
    }

    @Test
    @DisplayName("render — missing variable substitutes to empty + emits warn log")
    void render_missingVariableEmptyStringAndWarns(CapturedOutput output) {
        EmailTemplate template = newTemplate(
                "MissingKey",
                "Hello {{firstName}} {{lastName}}",
                "ignored",
                null,
                true);
        when(repository.findByTemplateKey("MissingKey")).thenReturn(Optional.of(template));

        RenderedEmail rendered = service.render("MissingKey", Map.of("firstName", "Ada"));

        // lastName not supplied — renders as empty + the listener emits a
        // warn log so the gap is observable in dev.
        assertThat(rendered.subject()).isEqualTo("Hello Ada ");
        assertThat(output.getOut()).contains("Email template variable 'lastName' has no value");
    }

    @Test
    @DisplayName("render — null variable map renders to a stable empty-substitution result")
    void render_nullVariableMap() {
        EmailTemplate template = newTemplate("NullKey", "Hi {{name}}", "<p>Hi {{name}}</p>", null, true);
        when(repository.findByTemplateKey("NullKey")).thenReturn(Optional.of(template));

        RenderedEmail rendered = service.render("NullKey", null);

        assertThat(rendered.subject()).isEqualTo("Hi ");
        assertThat(rendered.bodyHtml()).isEqualTo("<p>Hi </p>");
    }

    @Test
    @DisplayName("get — caches the lookup; second call does not hit the repository")
    void get_cachesLookup() {
        EmailTemplate template = newTemplate("CacheKey", "s", "<p>h</p>", "t", true);
        when(repository.findByTemplateKey("CacheKey")).thenReturn(Optional.of(template));

        service.get("CacheKey");
        service.get("CacheKey");
        service.get("CacheKey");

        // 3 service calls, but only the first one touched the repository.
        verify(repository, times(1)).findByTemplateKey("CacheKey");
    }

    @Test
    @DisplayName("update — saves the patch and evicts the cache so next get reloads")
    void update_evictsCache() {
        EmailTemplate original = newTemplate("EvictKey", "old", "<p>old</p>", "old", true);
        original.setId(42L);
        when(repository.findByTemplateKey("EvictKey")).thenReturn(Optional.of(original));
        when(repository.findById(42L)).thenReturn(Optional.of(original));
        when(repository.save(any(EmailTemplate.class))).thenAnswer(inv -> inv.getArgument(0));

        // Warm the cache.
        service.get("EvictKey");

        service.update(42L, new EmailTemplateUpdate("new", null, null, null, null), 7L);

        // Cache evicted — next get re-hits the repository.
        service.get("EvictKey");
        verify(repository, times(2)).findByTemplateKey("EvictKey");
    }

    @Test
    @DisplayName("get — throws EntityNotFoundException on unknown key")
    void get_throwsOnMissingKey() {
        when(repository.findByTemplateKey("Missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get("Missing"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Missing");
    }

    @Test
    @DisplayName("get — throws IllegalStateException when row is disabled")
    void get_throwsWhenDisabled() {
        EmailTemplate template = newTemplate("Disabled", "s", "<p>h</p>", "t", false);
        when(repository.findByTemplateKey("Disabled")).thenReturn(Optional.of(template));

        assertThatThrownBy(() -> service.get("Disabled"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("disabled");
    }

    @Test
    @DisplayName("renderPreview — works even when the row is disabled; returns empty on missing key")
    void renderPreview_disabledOkMissingEmpty() {
        EmailTemplate disabled = newTemplate("PreviewDisabled", "{{x}}", "<p>{{x}}</p>", "{{x}}", false);
        when(repository.findByTemplateKey("PreviewDisabled")).thenReturn(Optional.of(disabled));
        when(repository.findByTemplateKey("NoSuchKey")).thenReturn(Optional.empty());

        Optional<RenderedEmail> ok = service.renderPreview("PreviewDisabled", Map.of("x", "y"));
        Optional<RenderedEmail> missing = service.renderPreview("NoSuchKey", Map.of());

        assertThat(ok).isPresent();
        assertThat(ok.get().subject()).isEqualTo("y");
        assertThat(missing).isEmpty();
        // The disabled-render path must NOT consult the regular get()
        // cache — at most one repository call regardless.
        verify(repository, atMostOnce()).findByTemplateKey("PreviewDisabled");
    }

    @Test
    @DisplayName("render — preserves a Matcher.appendReplacement-hostile value ($ in substitution)")
    void render_substitutionWithDollarSign() {
        // The currency-formatted value carries a literal $ — Matcher's
        // appendReplacement treats $0..$9 as backreferences unless the
        // service quotes them. Regression-guards the quoteReplacement
        // call inside substitute().
        EmailTemplate template = newTemplate(
                "DollarKey",
                "Total {{amount}}",
                "<p>{{amount}}</p>",
                "Total {{amount}}",
                true);
        when(repository.findByTemplateKey("DollarKey")).thenReturn(Optional.of(template));

        RenderedEmail rendered = service.render("DollarKey", Map.of("amount", "$25.50"));

        // Subject + text body keep the raw $ value.
        assertThat(rendered.subject()).isEqualTo("Total $25.50");
        assertThat(rendered.bodyText()).isEqualTo("Total $25.50");
        // HTML body escapes nothing here ($ isn't a special HTML char).
        assertThat(rendered.bodyHtml()).isEqualTo("<p>$25.50</p>");
    }

    private static EmailTemplate newTemplate(
            String key, String subject, String bodyHtml, String bodyText, boolean enabled) {
        EmailTemplate t = new EmailTemplate();
        t.setTemplateKey(key);
        t.setSubject(subject);
        t.setBodyHtml(bodyHtml);
        t.setBodyText(bodyText);
        t.setEnabled(enabled);
        return t;
    }
}
