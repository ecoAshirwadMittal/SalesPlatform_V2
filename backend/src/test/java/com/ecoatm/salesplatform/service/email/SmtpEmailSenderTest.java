package com.ecoatm.salesplatform.service.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.Address;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Message.RecipientType;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Unit tests for {@link SmtpEmailSender}. Uses a mocked {@link JavaMailSender}
 * that hands back a real, in-memory {@link MimeMessage} so assertions inspect
 * the actual MIME headers/body that {@link org.springframework.mail.javamail.MimeMessageHelper}
 * wrote — no live SMTP server (e.g. GreenMail) needed for this unit-level coverage.
 */
class SmtpEmailSenderTest {

    private static final String FALLBACK_FROM = "noreply@ecoatmdirect.com";

    private JavaMailSender mailSender;
    private SmtpEmailSender sender;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        sender = new SmtpEmailSender(mailSender, FALLBACK_FROM);
    }

    @Test
    @DisplayName("uses the configured fallback From when the message supplies none")
    void usesFallbackFrom_whenMessageFromIsNull() throws Exception {
        EmailMessage msg = EmailMessage.of(
                List.of("buyer@example.com"), List.of(), "Subject", "<p>hi</p>", null);

        MimeMessage sent = captureSend(msg);

        assertThat(addressesOf(sent.getFrom())).containsExactly(FALLBACK_FROM);
    }

    @Test
    @DisplayName("uses the configured fallback From when the message's From is blank, not just null (T5 guard)")
    void usesFallbackFrom_whenMessageFromIsBlank() throws Exception {
        EmailMessage msg = new EmailMessage(
                List.of("buyer@example.com"), List.of(), List.of(),
                "   ", null, "Subject", "<p>hi</p>", null);

        MimeMessage sent = captureSend(msg);

        assertThat(addressesOf(sent.getFrom())).containsExactly(FALLBACK_FROM);
    }

    @Test
    @DisplayName("prefers the message's own From over the configured fallback")
    void prefersMessageFrom_overFallback() throws Exception {
        EmailMessage msg = new EmailMessage(
                List.of("buyer@example.com"), List.of(), List.of(),
                "custom@ecoatmdirect.com", null, "Subject", "<p>hi</p>", null);

        MimeMessage sent = captureSend(msg);

        assertThat(addressesOf(sent.getFrom())).containsExactly("custom@ecoatmdirect.com");
    }

    @Test
    @DisplayName("sets Reply-To when the message supplies one")
    void setsReplyTo_whenPresent() throws Exception {
        EmailMessage msg = new EmailMessage(
                List.of("buyer@example.com"), List.of(), List.of(),
                null, "sales@ecoatmdirect.com", "Subject", "<p>hi</p>", null);

        MimeMessage sent = captureSend(msg);

        assertThat(sent.getHeader("Reply-To")).containsExactly("sales@ecoatmdirect.com");
    }

    @Test
    @DisplayName("omits the Reply-To header when the message has none")
    void omitsReplyTo_whenAbsent() throws Exception {
        EmailMessage msg = EmailMessage.of(
                List.of("buyer@example.com"), List.of(), "Subject", "<p>hi</p>", null);

        MimeMessage sent = captureSend(msg);

        assertThat(sent.getHeader("Reply-To")).isNull();
    }

    @Test
    @DisplayName("omits the Reply-To header when the message's Reply-To is blank, not just null (T5 guard)")
    void omitsReplyTo_whenBlank() throws Exception {
        EmailMessage msg = new EmailMessage(
                List.of("buyer@example.com"), List.of(), List.of(),
                null, "   ", "Subject", "<p>hi</p>", null);

        MimeMessage sent = captureSend(msg);

        assertThat(sent.getHeader("Reply-To")).isNull();
    }

    @Test
    @DisplayName("sets To, Cc, and Bcc when all are supplied")
    void setsToCcAndBcc() throws Exception {
        EmailMessage msg = new EmailMessage(
                List.of("buyer@example.com"),
                List.of("cc@example.com"),
                List.of("bcc@example.com"),
                null, null, "Subject", "<p>hi</p>", null);

        MimeMessage sent = captureSend(msg);

        assertThat(addressesOf(sent.getRecipients(RecipientType.TO))).containsExactly("buyer@example.com");
        assertThat(addressesOf(sent.getRecipients(RecipientType.CC))).containsExactly("cc@example.com");
        assertThat(addressesOf(sent.getRecipients(RecipientType.BCC))).containsExactly("bcc@example.com");
    }

    @Test
    @DisplayName("omits Cc/Bcc recipients when the message has none")
    void omitsCcAndBcc_whenEmpty() throws Exception {
        EmailMessage msg = EmailMessage.of(
                List.of("buyer@example.com"), List.of(), "Subject", "<p>hi</p>", null);

        MimeMessage sent = captureSend(msg);

        assertThat(sent.getRecipients(RecipientType.CC)).isNull();
        assertThat(sent.getRecipients(RecipientType.BCC)).isNull();
    }

    @Test
    @DisplayName("delivers the HTML body and subject")
    void deliversHtmlBodyAndSubject() throws Exception {
        EmailMessage msg = EmailMessage.of(
                List.of("buyer@example.com"), List.of(), "Offer Confirmation",
                "<html><body><h1>Offer #42</h1></body></html>", null);

        MimeMessage sent = captureSend(msg);

        assertThat(sent.getSubject()).isEqualTo("Offer Confirmation");
        assertThat(collectText(sent)).contains("Offer #42");
    }

    @Test
    @DisplayName("delivers a plain-text alternative when provided")
    void deliversPlainTextAlternative() throws Exception {
        EmailMessage msg = EmailMessage.of(
                List.of("buyer@example.com"), List.of(), "Pending Order",
                "<p>HTML body</p>", "Plain text body");

        MimeMessage sent = captureSend(msg);

        String combined = collectText(sent);
        assertThat(combined).contains("HTML body");
        assertThat(combined).contains("Plain text body");
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    /** Sends {@code msg} through the real sender and returns the MimeMessage it built. */
    private MimeMessage captureSend(EmailMessage msg) throws Exception {
        MimeMessage mime = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mime);

        sender.send(msg);

        verify(mailSender).send(mime);
        return mime;
    }

    private static List<String> addressesOf(Address[] addresses) {
        return addresses == null ? List.of() : Stream.of(addresses).map(Address::toString).toList();
    }

    /** Recursively walks (possibly nested) multipart content and concatenates every text leaf. */
    private static String collectText(Part part) throws Exception {
        Object content = part.getContent();
        if (content instanceof Multipart multipart) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < multipart.getCount(); i++) {
                sb.append(collectText(multipart.getBodyPart(i)));
            }
            return sb.toString();
        }
        return String.valueOf(content);
    }
}
