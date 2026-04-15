package com.ecoatm.salesplatform.service.email;

import static org.assertj.core.api.Assertions.assertThat;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Unit-level integration test for {@link SmtpEmailSender} using an in-process
 * GreenMail SMTP server. Bypasses Spring context — wires the sender with a
 * real {@code JavaMailSenderImpl} pointed at the GreenMail port.
 */
class SmtpEmailSenderTest {

    @RegisterExtension
    static final GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"))
            .withPerMethodLifecycle(true);

    private SmtpEmailSender newSender() {
        JavaMailSenderImpl mail = new JavaMailSenderImpl();
        mail.setHost("localhost");
        mail.setPort(ServerSetupTest.SMTP.getPort());
        return new SmtpEmailSender(mail, "noreply@ecoatmdirect.com");
    }

    @Test
    @DisplayName("delivers HTML message to GreenMail inbox")
    void delivers_htmlMessage() throws Exception {
        SmtpEmailSender sender = newSender();
        EmailMessage msg = new EmailMessage(
                List.of("buyer@example.com"),
                List.of("sales@ecoatmdirect.com"),
                "Offer Confirmation",
                "<html><body><h1>Offer #42</h1></body></html>",
                null);

        sender.send(msg);

        assertThat(greenMail.waitForIncomingEmail(5000, 1)).isTrue();
        MimeMessage[] received = greenMail.getReceivedMessages();
        assertThat(received).hasSize(2); // one per recipient (to + cc)
        MimeMessage first = received[0];
        assertThat(first.getSubject()).isEqualTo("Offer Confirmation");
        assertThat(first.getFrom()[0].toString()).contains("noreply@ecoatmdirect.com");
        // Body is multipart (HTML); assert the HTML fragment appears in raw content
        String raw = com.icegreen.greenmail.util.GreenMailUtil.getBody(first);
        assertThat(raw).contains("Offer #42");
    }

    @Test
    @DisplayName("delivers plain-text alternative when provided")
    void delivers_multipartAlternative() throws Exception {
        SmtpEmailSender sender = newSender();
        EmailMessage msg = new EmailMessage(
                List.of("buyer@example.com"),
                List.of(),
                "Pending Order",
                "<p>HTML body</p>",
                "Plain text body");

        sender.send(msg);

        assertThat(greenMail.waitForIncomingEmail(5000, 1)).isTrue();
        MimeMessage received = greenMail.getReceivedMessages()[0];
        String raw = com.icegreen.greenmail.util.GreenMailUtil.getBody(received);
        assertThat(raw).contains("HTML body");
        assertThat(raw).contains("Plain text body");
    }
}
