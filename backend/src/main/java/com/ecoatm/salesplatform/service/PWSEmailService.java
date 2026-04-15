package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.config.AsyncConfig;
import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.service.email.EmailMessage;
import com.ecoatm.salesplatform.service.email.EmailSender;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * PWS email notifications — Mendix parity with four legacy microflows:
 *
 * <ul>
 *   <li>{@code SUB_SendPWSOfferConfirmationEmail} → {@link #sendOfferConfirmationEmail}</li>
 *   <li>{@code SUB_SendPWSOrderConfirmationEmail} → {@link #sendOrderConfirmationEmail}</li>
 *   <li>{@code SUB_SendPWSAdjustedQuantityOrderConfirmationEmail} → {@link #sendPendingOrderEmail}</li>
 *   <li>{@code SUB_SendPWSCounterOfferEmail} → {@link #sendCounterOfferEmail}</li>
 * </ul>
 *
 * <p>All methods are {@code @Async} on the dedicated email executor
 * ({@link AsyncConfig#EMAIL_EXECUTOR}) so they never run inside the caller's
 * transaction. Any delivery exception is caught and logged — failures must
 * not affect the business transaction that triggered them.
 *
 * <p>Template rendering uses Spring Boot's auto-configured
 * {@link TemplateEngine}; transport is abstracted behind {@link EmailSender}
 * so tests can swap in an in-process implementation.
 */
@Service
public class PWSEmailService {

    private static final Logger log = LoggerFactory.getLogger(PWSEmailService.class);

    private final TemplateEngine templateEngine;
    private final EmailSender emailSender;
    private final EcoATMDirectUserRepository directUserRepository;
    private final DeviceRepository deviceRepository;
    private final String salesAddress;
    private final String counterOfferUrl;

    public PWSEmailService(
            TemplateEngine templateEngine,
            EmailSender emailSender,
            EcoATMDirectUserRepository directUserRepository,
            DeviceRepository deviceRepository,
            @Value("${pws.email.sales-address}") String salesAddress,
            @Value("${pws.email.counter-offer-url}") String counterOfferUrl) {
        this.templateEngine = templateEngine;
        this.emailSender = emailSender;
        this.directUserRepository = directUserRepository;
        this.deviceRepository = deviceRepository;
        this.salesAddress = salesAddress;
        this.counterOfferUrl = counterOfferUrl;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Public API — one method per Mendix microflow.
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Mendix parity: {@code SUB_SendPWSOfferConfirmationEmail}.
     * Delivered only to the user who submitted the offer.
     */
    @Async(AsyncConfig.EMAIL_EXECUTOR)
    public void sendOfferConfirmationEmail(Offer offer, Long submittedByUserId) {
        safeSend("OfferConfirmation", offer, () -> {
            RecipientUser user = resolveSingleRecipient(submittedByUserId);
            if (user == null) {
                log.warn("OfferConfirmation skipped: no recipient resolved for userId={}", submittedByUserId);
                return null;
            }
            String company = resolveCompanyName(offer.getBuyerCodeId());
            List<ItemRow> rows = buildOfferRows(offer.getItems());
            BigDecimal total = nullSafe(offer.getTotalPrice());
            String html = render("email/pws-offer-confirmation",
                    baseContext(user.fullName(), offer, company, rows, total));
            return new EmailMessage(
                    List.of(user.email()),
                    List.of(),
                    "Offer Confirmation — " + displayOfferRef(offer),
                    html,
                    null);
        });
    }

    /**
     * Mendix parity: {@code SUB_SendPWSOrderConfirmationEmail}.
     * Delivered to every active user linked to the buyer code.
     */
    @Async(AsyncConfig.EMAIL_EXECUTOR)
    public void sendOrderConfirmationEmail(Offer offer) {
        safeSend("OrderConfirmation", offer, () -> {
            List<RecipientUser> recipients = resolveBuyerRecipients(offer.getBuyerCodeId());
            if (recipients.isEmpty()) {
                log.warn("OrderConfirmation skipped: no recipients for buyerCodeId={}", offer.getBuyerCodeId());
                return null;
            }
            String company = resolveCompanyName(offer.getBuyerCodeId());
            List<ItemRow> rows = buildFinalRows(offer.getItems());
            BigDecimal total = rows.stream().map(ItemRow::totalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            String html = render("email/pws-order-confirmation",
                    baseContext(firstOr(recipients).fullName(), offer, company, rows, total));
            return new EmailMessage(
                    recipients.stream().map(RecipientUser::email).toList(),
                    List.of(),
                    "Order Confirmation — " + displayOfferRef(offer),
                    html,
                    null);
        });
    }

    /**
     * Mendix parity: {@code SUB_SendPWSAdjustedQuantityOrderConfirmationEmail}.
     * Sent when Oracle adjusts quantities on an order; goes to buyer users + CC sales.
     */
    @Async(AsyncConfig.EMAIL_EXECUTOR)
    public void sendPendingOrderEmail(Offer offer) {
        safeSend("PendingOrder", offer, () -> {
            List<RecipientUser> recipients = resolveBuyerRecipients(offer.getBuyerCodeId());
            if (recipients.isEmpty()) {
                log.warn("PendingOrder skipped: no recipients for buyerCodeId={}", offer.getBuyerCodeId());
                return null;
            }
            String company = resolveCompanyName(offer.getBuyerCodeId());
            List<ItemRow> rows = buildFinalRows(offer.getItems());
            BigDecimal total = rows.stream().map(ItemRow::totalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            String html = render("email/pws-pending-order",
                    baseContext(firstOr(recipients).fullName(), offer, company, rows, total));
            return new EmailMessage(
                    recipients.stream().map(RecipientUser::email).toList(),
                    List.of(salesAddress),
                    "Order Pending — " + displayOfferRef(offer),
                    html,
                    null);
        });
    }

    /**
     * Mendix parity: {@code SUB_SendPWSCounterOfferEmail}.
     * Fans out to every buyer user and includes a "See Counter Offer" CTA.
     */
    @Async(AsyncConfig.EMAIL_EXECUTOR)
    public void sendCounterOfferEmail(Offer offer) {
        safeSend("CounterOffer", offer, () -> {
            List<RecipientUser> recipients = resolveBuyerRecipients(offer.getBuyerCodeId());
            if (recipients.isEmpty()) {
                log.warn("CounterOffer skipped: no recipients for buyerCodeId={}", offer.getBuyerCodeId());
                return null;
            }
            String company = resolveCompanyName(offer.getBuyerCodeId());
            List<ItemRow> rows = buildCounterRows(offer.getItems());
            BigDecimal total = rows.stream().map(ItemRow::totalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            Context ctx = baseContext(firstOr(recipients).fullName(), offer, company, rows, total);
            ctx.setVariable("counterOfferUrl", counterOfferUrl);
            String html = render("email/pws-counter-offer", ctx);
            return new EmailMessage(
                    recipients.stream().map(RecipientUser::email).toList(),
                    List.of(),
                    "Counter Offer — " + displayOfferRef(offer),
                    html,
                    null);
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Internals
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Wraps the build-and-send pipeline with uniform error handling. The
     * supplier may return {@code null} to skip the send (e.g. no recipients
     * resolved); any thrown exception is caught and logged but not rethrown.
     */
    private void safeSend(String templateName, Offer offer, MessageBuilder builder) {
        try {
            EmailMessage message = builder.build();
            if (message == null) {
                return;
            }
            emailSender.send(message);
        } catch (Exception ex) {
            log.error(
                    "PWS email delivery failed template={} offerId={} offerNumber={}: {}",
                    templateName,
                    offer == null ? null : offer.getId(),
                    offer == null ? null : offer.getOfferNumber(),
                    ex.getMessage(),
                    ex);
        }
    }

    @FunctionalInterface
    private interface MessageBuilder {
        EmailMessage build();
    }

    private Context baseContext(
            String buyerName, Offer offer, String companyName, List<ItemRow> rows, BigDecimal total) {
        Context ctx = new Context();
        ctx.setVariable("buyerName", buyerName == null ? "Valued Customer" : buyerName);
        ctx.setVariable("offerId", displayOfferRef(offer));
        ctx.setVariable("companyName", companyName == null ? "" : companyName);
        ctx.setVariable("items", rows);
        ctx.setVariable("total", total);
        return ctx;
    }

    private String render(String templateName, Context ctx) {
        return templateEngine.process(templateName, ctx);
    }

    private String displayOfferRef(Offer offer) {
        if (offer == null) return "";
        return offer.getOfferNumber() != null ? offer.getOfferNumber() : String.valueOf(offer.getId());
    }

    // ── Row builders ────────────────────────────────────────────────────────

    private List<ItemRow> buildOfferRows(List<OfferItem> items) {
        Map<Long, Device> deviceMap = loadDeviceMap(items);
        List<ItemRow> rows = new ArrayList<>();
        for (OfferItem item : items) {
            Device device = deviceMap.get(item.getDeviceId());
            rows.add(new ItemRow(
                    skuOf(device, item),
                    describeDevice(device),
                    nullSafeInt(item.getQuantity()),
                    nullSafe(item.getPrice()),
                    nullSafe(item.getTotalPrice())));
        }
        return rows;
    }

    private List<ItemRow> buildFinalRows(List<OfferItem> items) {
        Map<Long, Device> deviceMap = loadDeviceMap(items);
        List<ItemRow> rows = new ArrayList<>();
        for (OfferItem item : items) {
            Device device = deviceMap.get(item.getDeviceId());
            int qty = item.getFinalOfferQuantity() != null ? item.getFinalOfferQuantity() : nullSafeInt(item.getQuantity());
            BigDecimal price = item.getFinalOfferPrice() != null ? item.getFinalOfferPrice() : nullSafe(item.getPrice());
            BigDecimal total = item.getFinalOfferTotalPrice() != null ? item.getFinalOfferTotalPrice() : nullSafe(item.getTotalPrice());
            rows.add(new ItemRow(skuOf(device, item), describeDevice(device), qty, price, total));
        }
        return rows;
    }

    private List<ItemRow> buildCounterRows(List<OfferItem> items) {
        Map<Long, Device> deviceMap = loadDeviceMap(items);
        List<ItemRow> rows = new ArrayList<>();
        for (OfferItem item : items) {
            Device device = deviceMap.get(item.getDeviceId());
            String status = item.getItemStatus();
            int qty;
            BigDecimal price;
            BigDecimal total;
            if ("Accept".equals(status)) {
                qty = nullSafeInt(item.getQuantity());
                price = nullSafe(item.getPrice());
                total = nullSafe(item.getTotalPrice());
            } else if ("Counter".equals(status) || "Finalize".equals(status)) {
                qty = nullSafeInt(item.getCounterQty());
                price = nullSafe(item.getCounterPrice());
                total = nullSafe(item.getCounterTotal());
            } else {
                // Decline / empty → zero row, legacy parity
                qty = 0;
                price = BigDecimal.ZERO;
                total = BigDecimal.ZERO;
            }
            rows.add(new ItemRow(skuOf(device, item), describeDevice(device), qty, price, total));
        }
        return rows;
    }

    // ── Recipient resolution ────────────────────────────────────────────────

    private RecipientUser resolveSingleRecipient(Long userId) {
        if (userId == null) return null;
        List<Object[]> rows = directUserRepository.findEmailByUserId(userId);
        if (rows.isEmpty()) return null;
        Object[] row = rows.get(0);
        return new RecipientUser((String) row[0], (String) row[1]);
    }

    private List<RecipientUser> resolveBuyerRecipients(Long buyerCodeId) {
        if (buyerCodeId == null) return List.of();
        return directUserRepository.findActiveEmailsByBuyerCodeId(buyerCodeId).stream()
                .map(row -> new RecipientUser((String) row[0], (String) row[1]))
                .toList();
    }

    private String resolveCompanyName(Long buyerCodeId) {
        if (buyerCodeId == null) return "";
        List<String> names = directUserRepository.findBuyerCompanyNameByBuyerCodeId(buyerCodeId);
        return names.isEmpty() ? "" : names.get(0);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private Map<Long, Device> loadDeviceMap(List<OfferItem> items) {
        Set<Long> ids = items.stream()
                .map(OfferItem::getDeviceId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) return Collections.emptyMap();
        return deviceRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Device::getId, d -> d));
    }

    private String skuOf(Device device, OfferItem item) {
        if (device != null && device.getSku() != null) return device.getSku();
        return item.getSku() != null ? item.getSku() : "";
    }

    private String describeDevice(Device device) {
        if (device == null) return "";
        List<String> parts = new ArrayList<>(4);
        if (device.getModel() != null) parts.add(device.getModel().getDisplayName());
        if (device.getCarrier() != null) parts.add(device.getCarrier().getDisplayName());
        if (device.getCapacity() != null) parts.add(device.getCapacity().getDisplayName());
        if (device.getColor() != null) parts.add(device.getColor().getDisplayName());
        return String.join(", ", parts);
    }

    private static BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private static int nullSafeInt(Integer value) {
        return value != null ? value : 0;
    }

    private static RecipientUser firstOr(List<RecipientUser> list) {
        return list.isEmpty() ? new RecipientUser("", "Valued Customer") : list.get(0);
    }

    // ── View model records (public so templates can resolve bean properties) ──

    /** Row model consumed by {@code email/_rows.html}. */
    public record ItemRow(
            String sku, String description, int quantity, BigDecimal unitPrice, BigDecimal totalPrice) {}

    /** Resolved recipient pair used for TO/CC lists. */
    public record RecipientUser(String email, String fullName) {}
}
