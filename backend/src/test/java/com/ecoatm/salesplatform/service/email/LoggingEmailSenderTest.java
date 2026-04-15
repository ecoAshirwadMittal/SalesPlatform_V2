package com.ecoatm.salesplatform.service.email;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
class LoggingEmailSenderTest {

    private final LoggingEmailSender sender = new LoggingEmailSender();

    @Test
    @DisplayName("logs recipients, subject, and body sizes")
    void logs_messageSummary(CapturedOutput output) {
        EmailMessage m = new EmailMessage(
                List.of("buyer@example.com"),
                List.of("cc@example.com"),
                "Test Subject",
                "<p>hello</p>",
                "hello");

        sender.send(m);

        assertThat(output.getOut()).contains("LoggingEmailSender");
        assertThat(output.getOut()).contains("buyer@example.com");
        assertThat(output.getOut()).contains("cc@example.com");
        assertThat(output.getOut()).contains("Test Subject");
        assertThat(output.getOut()).contains("htmlBytes=12");
        assertThat(output.getOut()).contains("textBytes=5");
    }
}
