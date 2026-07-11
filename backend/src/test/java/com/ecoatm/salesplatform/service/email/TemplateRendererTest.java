package com.ecoatm.salesplatform.service.email;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

/**
 * Unit tests for {@link TemplateRenderer}. Pure POJO — no Spring context,
 * mirrors the brief's exact cases plus the two extra modes the extraction
 * introduces ({@code renderPlain} and the null/empty-template guard).
 *
 * <p>These cases mirror (byte-for-byte) the pre-extraction behavior
 * asserted by {@code EmailTemplateServiceTest} in the partialcredit
 * package; that test now delegates to this class and remains the
 * regression gate proving the extraction changed nothing observable.
 */
@ExtendWith(OutputCaptureExtension.class)
class TemplateRendererTest {

    @Test
    @DisplayName("render — escapes by default, {{!var}} opts out, missing key renders empty")
    void escapesByDefault_rawOptIn_missingEmpty() {
        var r = new TemplateRenderer();
        assertThat(r.render("Hi {{name}}", Map.of("name", "<b>&x</b>")))
                .isEqualTo("Hi &lt;b&gt;&amp;x&lt;/b&gt;");
        assertThat(r.render("{{!raw}}", Map.of("raw", "<a href=\"#\">L</a>")))
                .isEqualTo("<a href=\"#\">L</a>");
        assertThat(r.render("[{{missing}}]", Map.of())).isEqualTo("[]");
    }

    @Test
    @DisplayName("render — $ in a substituted value does not break Matcher.appendReplacement")
    void dollarInValueDoesNotBreak() {
        assertThat(new TemplateRenderer().render("Owed {{amt}}", Map.of("amt", "$5")))
                .isEqualTo("Owed $5");
    }

    @Test
    @DisplayName("renderPlain — never escapes, even without the {{!var}} raw marker")
    void renderPlain_neverEscapes() {
        var r = new TemplateRenderer();
        assertThat(r.renderPlain("Hi {{name}}", Map.of("name", "<b>&x</b>")))
                .isEqualTo("Hi <b>&x</b>");
        assertThat(r.renderPlain("Owed {{amt}}", Map.of("amt", "$5")))
                .isEqualTo("Owed $5");
    }

    @Test
    @DisplayName("render/renderPlain — null or empty template is returned unchanged")
    void nullOrEmptyTemplate_returnedUnchanged() {
        var r = new TemplateRenderer();
        assertThat(r.render(null, Map.of())).isNull();
        assertThat(r.render("", Map.of())).isEqualTo("");
        assertThat(r.renderPlain(null, Map.of())).isNull();
        assertThat(r.renderPlain("", Map.of())).isEqualTo("");
    }

    @Test
    @DisplayName("render — missing variable emits a warn log in addition to rendering empty")
    void missingVariable_emitsWarnLog(CapturedOutput output) {
        var r = new TemplateRenderer();

        String result = r.render("Hello {{firstName}} {{lastName}}", Map.of("firstName", "Ada"));

        assertThat(result).isEqualTo("Hello Ada ");
        assertThat(output.getOut()).contains("Email template variable 'lastName' has no value");
    }
}
