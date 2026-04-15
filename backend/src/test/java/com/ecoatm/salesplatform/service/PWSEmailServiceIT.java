package com.ecoatm.salesplatform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.service.email.SmtpEmailSender;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * End-to-end test for the PWS email pipeline — wires a real
 * {@link TemplateEngine}, {@link SmtpEmailSender}, and
 * {@link com.icegreen.greenmail.junit5.GreenMailExtension} together, with
 * mocked repositories, to verify that a single {@code PWSEmailService} call
 * produces a fully-rendered multipart message at the SMTP inbox — covering
 * template rendering + SMTP transport + Mendix-parity subject/body content.
 *
 * <p>Skips the Spring context, JPA, and async executor so the test stays
 * fast and deterministic. Unit tests ({@code PWSEmailServiceTest}) already
 * cover recipient resolution branching with Mockito.
 */
class PWSEmailServiceIT {

    @RegisterExtension
    static final GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"))
            .withPerMethodLifecycle(true);

    private PWSEmailService newService() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);

        JavaMailSenderImpl mail = new JavaMailSenderImpl();
        mail.setHost("localhost");
        mail.setPort(ServerSetupTest.SMTP.getPort());
        SmtpEmailSender sender = new SmtpEmailSender(mail, "noreply@ecoatmdirect.com");

        EcoATMDirectUserRepository directUserRepo = mock(EcoATMDirectUserRepository.class);
        DeviceRepository deviceRepo = mock(DeviceRepository.class);
        when(deviceRepo.findAllById(any())).thenReturn(Collections.emptyList());
        when(directUserRepo.findActiveEmailsByBuyerCodeId(eq(10L)))
                .thenReturn(List.<Object[]>of(new Object[] {"buyer@example.com", "Alice Buyer"}));
        when(directUserRepo.findBuyerCompanyNameByBuyerCodeId(eq(10L)))
                .thenReturn(List.of("Acme Corp"));

        return new PWSEmailService(
                engine,
                sender,
                directUserRepo,
                deviceRepo,
                "sales@ecoatmdirect.com",
                "https://buy.ecoatmdirect.com/p/counter-offer/");
    }

    private Offer sampleOffer() {
        Offer offer = new Offer();
        offer.setId(42L);
        offer.setOfferNumber("OFR-42");
        offer.setBuyerCodeId(10L);
        offer.setTotalPrice(new BigDecimal("1000.00"));

        OfferItem item = new OfferItem();
        item.setSku("SKU-001");
        item.setQuantity(5);
        item.setPrice(new BigDecimal("200.00"));
        item.setTotalPrice(new BigDecimal("1000.00"));
        offer.setItems(new java.util.ArrayList<>(List.of(item)));
        return offer;
    }

    @Test
    @DisplayName("order confirmation flows through template + SMTP end-to-end")
    void orderConfirmation_endToEnd() throws Exception {
        PWSEmailService service = newService();

        service.sendOrderConfirmationEmail(sampleOffer());

        assertThat(greenMail.waitForIncomingEmail(5000, 1)).isTrue();
        MimeMessage[] received = greenMail.getReceivedMessages();
        assertThat(received).isNotEmpty();
        MimeMessage msg = received[0];
        assertThat(msg.getSubject()).isEqualTo("Order Confirmation — OFR-42");
        assertThat(msg.getFrom()[0].toString()).contains("noreply@ecoatmdirect.com");

        String body = GreenMailUtil.getBody(msg);
        // Mendix parity markers from the rendered template
        assertThat(body).contains("Order Confirmation");
        assertThat(body).contains("Alice Buyer");
        assertThat(body).contains("Acme Corp");
        assertThat(body).contains("SKU-001");
        assertThat(body).contains("Trebuchet MS");
        assertThat(body).contains("#514F4E");
        assertThat(body).contains("ecoATM, LLC");
    }
}
