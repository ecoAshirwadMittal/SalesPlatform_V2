package com.ecoatm.salesplatform.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

/**
 * SMTP-backed {@link EmailSender} used when {@code pws.email.enabled=true}.
 *
 * <p>Retries transient failures up to 3 times with exponential backoff
 * (2s, 4s, 8s). After exhaustion the exception propagates to the caller,
 * which must catch it so the business transaction is not affected.
 */
@Component
@ConditionalOnProperty(name = "pws.email.enabled", havingValue = "true")
public class SmtpEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailSender.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public SmtpEmailSender(
            JavaMailSender mailSender,
            @Value("${pws.email.from}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    @Retryable(
            retryFor = {MailException.class, MessagingException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2))
    public void send(EmailMessage message) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            // multipart=true → enables HTML body + optional plain-text alternative
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(message.to().toArray(String[]::new));
            if (!message.cc().isEmpty()) {
                helper.setCc(message.cc().toArray(String[]::new));
            }
            helper.setSubject(message.subject());
            // Spring picks the right alternative order when both are supplied
            if (message.textBody() != null && !message.textBody().isBlank()) {
                helper.setText(message.textBody(), message.htmlBody());
            } else {
                helper.setText(message.htmlBody(), true);
            }
            mailSender.send(mime);
            log.info("[SmtpEmailSender] sent to={} subject=\"{}\"", message.to(), message.subject());
        } catch (MessagingException ex) {
            log.warn("[SmtpEmailSender] MIME build failed subject=\"{}\": {}", message.subject(), ex.getMessage());
            throw new MailDeliveryException("Failed to build MIME message", ex);
        }
    }

    /** Wraps checked {@link MessagingException} so callers can handle a single type. */
    public static class MailDeliveryException extends RuntimeException {
        public MailDeliveryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
