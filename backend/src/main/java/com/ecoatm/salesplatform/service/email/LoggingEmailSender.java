package com.ecoatm.salesplatform.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Default {@link EmailSender} used when {@code pws.email.enabled} is unset or
 * false — logs the message at INFO instead of hitting a real SMTP server.
 *
 * <p>Lets dev/test environments exercise the full email pipeline (recipient
 * resolution, template rendering, event wiring) without SMTP credentials.
 */
@Component
@ConditionalOnProperty(name = "pws.email.enabled", havingValue = "false", matchIfMissing = true)
public class LoggingEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailSender.class);

    @Override
    public void send(EmailMessage message) {
        log.info(
                "[LoggingEmailSender] to={} cc={} subject=\"{}\" htmlBytes={} textBytes={}",
                message.to(),
                message.cc(),
                message.subject(),
                message.htmlBody().length(),
                message.textBody() == null ? 0 : message.textBody().length());
        if (log.isDebugEnabled()) {
            log.debug("[LoggingEmailSender] htmlBody:\n{}", message.htmlBody());
        }
    }
}
