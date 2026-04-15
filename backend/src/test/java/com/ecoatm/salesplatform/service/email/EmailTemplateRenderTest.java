package com.ecoatm.salesplatform.service.email;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Renders each PWS email template with a fixed context and asserts Mendix
 * parity markers are present: Trebuchet font, #514F4E text color, #B7B5B5
 * row borders, #2CB34A CTA (counter-offer only), and formatted totals.
 *
 * <p>No Spring context — constructs a bare Thymeleaf engine pointed at the
 * classpath so the test is fast and isolated from Spring autoconfig changes.
 */
class EmailTemplateRenderTest {

    /** Minimal item row shape expected by {@code _rows.html}. */
    public record ItemRow(String sku, String description, int quantity,
                          BigDecimal unitPrice, BigDecimal totalPrice) {}

    private TemplateEngine engine;

    @BeforeEach
    void setUp() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);
        engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
    }

    private Context baseContext() {
        Context ctx = new Context();
        ctx.setVariables(Map.of(
                "buyerName", "Alice Buyer",
                "offerId", 42L,
                "companyName", "Acme Corp",
                "total", new BigDecimal("1234.50"),
                "items", List.of(
                        new ItemRow("SKU-001", "iPhone 14 Pro 128GB Unlocked Black",
                                5, new BigDecimal("200.00"), new BigDecimal("1000.00")),
                        new ItemRow("SKU-002", "Galaxy S23 256GB Verizon Gold",
                                2, new BigDecimal("117.25"), new BigDecimal("234.50")))));
        return ctx;
    }

    @Test
    @DisplayName("offer confirmation renders with styling parity markers")
    void offerConfirmation_rendersCorrectly() {
        String html = engine.process("email/pws-offer-confirmation", baseContext());

        assertThat(html).contains("Offer Confirmation");
        assertThat(html).contains("Alice Buyer");
        assertThat(html).contains("#42");
        assertThat(html).contains("Acme Corp");
        assertThat(html).contains("Trebuchet MS");
        assertThat(html).contains("#514F4E");
        assertThat(html).contains("#B7B5B5");
        assertThat(html).contains("SKU-001");
        assertThat(html).contains("iPhone 14 Pro 128GB");
        assertThat(html).contains("$1,000");
        assertThat(html).contains("$1,234"); // total
        assertThat(html).contains("ecoATM, LLC");
    }

    @Test
    @DisplayName("order confirmation renders with confirmed copy")
    void orderConfirmation_rendersCorrectly() {
        String html = engine.process("email/pws-order-confirmation", baseContext());

        assertThat(html).contains("Order Confirmation");
        assertThat(html).contains("has been confirmed");
        assertThat(html).contains("SKU-001");
        assertThat(html).contains("$1,234");
    }

    @Test
    @DisplayName("pending order renders with adjusted-quantities copy")
    void pendingOrder_rendersCorrectly() {
        String html = engine.process("email/pws-pending-order", baseContext());

        assertThat(html).contains("Order Pending");
        assertThat(html).contains("Adjusted Quantities");
        assertThat(html).contains("adjusted based on current availability");
        assertThat(html).contains("$1,234");
    }

    @Test
    @DisplayName("counter offer renders CTA button with URL")
    void counterOffer_rendersCtaButton() {
        Context ctx = baseContext();
        ctx.setVariable("counterOfferUrl", "https://buy.ecoatmdirect.com/p/counter-offer/");

        String html = engine.process("email/pws-counter-offer", ctx);

        assertThat(html).contains("Counter Offer Ready for Review");
        assertThat(html).contains("See Counter Offer");
        assertThat(html).contains("#2CB34A"); // CTA accent
        assertThat(html).contains("https://buy.ecoatmdirect.com/p/counter-offer/42");
    }
}
