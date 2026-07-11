package com.ecoatm.salesplatform.service.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmailMessageTest {

    @Test
    @DisplayName("rejects empty recipient list")
    void rejects_emptyTo() {
        assertThatThrownBy(() ->
                new EmailMessage(List.of(), List.of(), List.of(), null, null, "s", "<p/>", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("rejects empty recipient list (brief's exact case, explicit empty cc/bcc)")
    void emptyTo_throws() {
        assertThatThrownBy(() ->
                new EmailMessage(List.of(), List.of(), List.of(), null, null, "s", "<p>h</p>", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("rejects blank subject")
    void rejects_blankSubject() {
        assertThatThrownBy(() ->
                new EmailMessage(List.of("a@b.c"), List.of(), List.of(), null, null, " ", "<p/>", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("rejects blank htmlBody")
    void rejects_blankHtml() {
        assertThatThrownBy(() ->
                new EmailMessage(List.of("a@b.c"), List.of(), List.of(), null, null, "sub", "", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("null cc/bcc normalise to empty lists; from/replyTo stay optional")
    void ccAndBccDefaultToEmpty_fromOptional() {
        var m = new EmailMessage(List.of("a@x.com"), null, null, null, null, "s", "<p>h</p>", null);
        assertThat(m.cc()).isEmpty();
        assertThat(m.bcc()).isEmpty();
        assertThat(m.from()).isNull();
        assertThat(m.replyTo()).isNull();
    }

    @Test
    @DisplayName("from and replyTo are retained when supplied")
    void fromAndReplyTo_areRetained() {
        EmailMessage m = new EmailMessage(
                List.of("a@b.c"), List.of(), List.of(),
                "custom@ecoatmdirect.com", "sales@ecoatmdirect.com",
                "sub", "<p/>", null);
        assertThat(m.from()).isEqualTo("custom@ecoatmdirect.com");
        assertThat(m.replyTo()).isEqualTo("sales@ecoatmdirect.com");
    }

    @Test
    @DisplayName("recipient/cc/bcc lists are defensively copied and immutable")
    void lists_areImmutable() {
        List<String> to = new ArrayList<>(List.of("a@b.c"));
        List<String> cc = new ArrayList<>(List.of("cc@b.c"));
        List<String> bcc = new ArrayList<>(List.of("bcc@b.c"));
        EmailMessage m = new EmailMessage(to, cc, bcc, null, null, "sub", "<p/>", null);

        to.add("mutated@evil.com");
        cc.add("mutated@evil.com");
        bcc.add("mutated@evil.com");

        assertThat(m.to()).containsExactly("a@b.c");
        assertThat(m.cc()).containsExactly("cc@b.c");
        assertThat(m.bcc()).containsExactly("bcc@b.c");
        assertThatThrownBy(() -> m.to().add("nope@x.y")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> m.cc().add("nope@x.y")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> m.bcc().add("nope@x.y")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("of() compat factory maps the 5 legacy args with bcc empty and from/replyTo null")
    void of_compatFactory_mapsFields() {
        EmailMessage m = EmailMessage.of(List.of("a@b.c"), List.of("cc@b.c"), "sub", "<p/>", "text");

        assertThat(m.to()).containsExactly("a@b.c");
        assertThat(m.cc()).containsExactly("cc@b.c");
        assertThat(m.bcc()).isEmpty();
        assertThat(m.from()).isNull();
        assertThat(m.replyTo()).isNull();
        assertThat(m.subject()).isEqualTo("sub");
        assertThat(m.htmlBody()).isEqualTo("<p/>");
        assertThat(m.textBody()).isEqualTo("text");
    }
}
