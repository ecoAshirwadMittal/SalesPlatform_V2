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
        assertThatThrownBy(() -> new EmailMessage(List.of(), List.of(), "s", "<p/>", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("rejects blank subject")
    void rejects_blankSubject() {
        assertThatThrownBy(() -> new EmailMessage(List.of("a@b.c"), List.of(), " ", "<p/>", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("rejects blank htmlBody")
    void rejects_blankHtml() {
        assertThatThrownBy(() -> new EmailMessage(List.of("a@b.c"), List.of(), "sub", "", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("null cc is normalised to empty list")
    void nullCc_toEmpty() {
        EmailMessage m = new EmailMessage(List.of("a@b.c"), null, "sub", "<p/>", null);
        assertThat(m.cc()).isEmpty();
    }

    @Test
    @DisplayName("recipient lists are defensively copied")
    void lists_areImmutable() {
        List<String> to = new ArrayList<>(List.of("a@b.c"));
        EmailMessage m = new EmailMessage(to, null, "sub", "<p/>", null);
        to.add("mutated@evil.com");
        assertThat(m.to()).containsExactly("a@b.c");
        assertThatThrownBy(() -> m.to().add("nope@x.y"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
