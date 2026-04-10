package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Mirrors the four legacy Mendix PWS email microflows:
 *   - SUB_SendPWSOfferConfirmationEmail (offer submitted for sales review)
 *   - SUB_SendPWSOrderConfirmationEmail (order confirmed by Oracle)
 *   - SUB_SendPWSPendingOrderEmail (order pending / Oracle error)
 *   - SUB_SendPWSCounterOfferEmail (counter offer sent to buyer)
 *
 * Currently logs the rendered HTML. When SMTP is configured, swap the
 * log.info call for JavaMailSender.send() or an external email API.
 */
@Service
public class PWSEmailService {

    private static final Logger log = LoggerFactory.getLogger(PWSEmailService.class);
    private static final NumberFormat CURRENCY_FMT = NumberFormat.getIntegerInstance(Locale.US);

    private final DeviceRepository deviceRepository;

    @Value("${pws.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${pws.email.sales-address:pwssales@ecoatm.com}")
    private String salesEmailAddress;

    @Value("${pws.email.counter-offer-url:https://buy.ecoatmdirect.com/pws/counter-offers?offerId=}")
    private String counterOfferBaseUrl;

    public PWSEmailService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    /**
     * SUB_SendPWSOfferConfirmationEmail — sent to the submitting buyer when an offer
     * is submitted for sales review. Uses OfferPrice/OfferQuantity.
     */
    public void sendOfferConfirmationEmail(Offer offer, String buyerName, String buyerEmail, String companyName) {
        Map<Long, Device> deviceMap = loadDeviceMap(offer.getItems());
        String htmlTable = buildItemTable(offer.getItems(), deviceMap, PriceSource.OFFER);
        BigDecimal total = offer.getTotalPrice() != null ? offer.getTotalPrice() : BigDecimal.ZERO;

        String subject = "PWS Offer Confirmation - " + offer.getOfferNumber();
        String body = wrapEmailBody(
                "Offer Confirmation",
                buyerName,
                companyName,
                offer.getOfferNumber(),
                htmlTable,
                total,
                null
        );

        sendOrLog("PWSOfferConfirmation", buyerEmail, subject, body);
    }

    /**
     * SUB_SendPWSOrderConfirmationEmail — sent to all buyer users linked to the
     * buyer code when an order is successfully placed. Uses FinalOfferPrice/FinalOfferQuantity.
     */
    public void sendOrderConfirmationEmail(Offer offer, String companyName) {
        Map<Long, Device> deviceMap = loadDeviceMap(offer.getItems());
        String htmlTable = buildItemTable(offer.getItems(), deviceMap, PriceSource.FINAL);
        BigDecimal total = calcTotal(offer.getItems(), PriceSource.FINAL);

        String subject = "PWS Order Confirmation - " + offer.getOfferNumber();

        // Legacy sends to all EcoATMDirectUsers linked to the buyer
        // For now, log with a note about recipients
        String body = wrapEmailBody(
                "Order Confirmation",
                "Valued Customer",
                companyName,
                offer.getOfferNumber(),
                htmlTable,
                total,
                null
        );

        sendOrLog("PWSOrderConfirmation", "[all buyer users]", subject, body);
    }

    /**
     * SUB_SendPWSPendingOrderEmail — sent to the sales email address when an order
     * encounters an Oracle error. Uses FinalOfferPrice/FinalOfferQuantity.
     */
    public void sendPendingOrderEmail(Offer offer, String companyName) {
        Map<Long, Device> deviceMap = loadDeviceMap(offer.getItems());
        String htmlTable = buildItemTable(offer.getItems(), deviceMap, PriceSource.FINAL);
        BigDecimal total = calcTotal(offer.getItems(), PriceSource.FINAL);

        String subject = "PWS Pending Order - " + offer.getOfferNumber();
        String body = wrapEmailBody(
                "Pending Order",
                "Sales Team",
                companyName,
                offer.getOfferNumber(),
                htmlTable,
                total,
                null
        );

        sendOrLog("PWSPendingOrder", salesEmailAddress, subject, body);
    }

    /**
     * SUB_SendPWSCounterOfferEmail — sent to all buyer users when a sales rep submits
     * counter-offers. Uses item status to choose which price columns to show.
     */
    public void sendCounterOfferEmail(Offer offer, String companyName) {
        Map<Long, Device> deviceMap = loadDeviceMap(offer.getItems());
        String htmlTable = buildCounterOfferTable(offer.getItems(), deviceMap);
        BigDecimal total = calcCounterTotal(offer.getItems());

        String counterButton = "<div style=\"text-align: center;\">"
                + "<a href=\"" + counterOfferBaseUrl + offer.getOfferNumber()
                + "\" style=\"background-color: #2CB34A; color: #ffffff; padding: 10px 16px; "
                + "text-decoration: none; border-radius: 4px; display: ruby; font-size: 16px; "
                + "font-family: Trebuchet MS, Helvetica, sans-serif; font-weight: bold;\">"
                + "See Counter Offer</a></div>";

        String subject = "PWS Counter Offer - " + offer.getOfferNumber();
        String body = wrapEmailBody(
                "Counter Offer",
                "Valued Customer",
                companyName,
                offer.getOfferNumber(),
                htmlTable,
                total,
                counterButton
        );

        sendOrLog("PWSCounterOffer", "[all buyer users]", subject, body);
    }

    // ---- HTML builders (matching Mendix templates) ----

    private enum PriceSource { OFFER, FINAL }

    private String buildItemTable(List<OfferItem> items, Map<Long, Device> deviceMap, PriceSource source) {
        StringBuilder html = new StringBuilder();
        html.append(TABLE_HEADER);
        for (OfferItem item : items) {
            Device d = deviceMap.get(item.getDeviceId());
            String desc = buildDescription(d);
            String sku = d != null ? d.getSku() : (item.getSku() != null ? item.getSku() : "");
            int qty = item.getQuantity() != null ? item.getQuantity() : 0;
            BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
            BigDecimal totalPrice = item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO;

            html.append(tableRow(sku, desc, qty, price, totalPrice));
        }
        html.append("</table>");
        return html.toString();
    }

    private String buildCounterOfferTable(List<OfferItem> items, Map<Long, Device> deviceMap) {
        StringBuilder html = new StringBuilder();
        html.append(TABLE_HEADER);
        for (OfferItem item : items) {
            Device d = deviceMap.get(item.getDeviceId());
            String desc = buildDescription(d);
            String sku = d != null ? d.getSku() : (item.getSku() != null ? item.getSku() : "");
            String status = item.getItemStatus();

            int qty;
            BigDecimal price;
            BigDecimal total;

            if ("Accept".equals(status)) {
                qty = item.getQuantity() != null ? item.getQuantity() : 0;
                price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
                total = item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO;
            } else if ("Counter".equals(status) || "Finalize".equals(status)) {
                qty = item.getCounterQty() != null ? item.getCounterQty() : 0;
                price = item.getCounterPrice() != null ? item.getCounterPrice() : BigDecimal.ZERO;
                total = item.getCounterTotal() != null ? item.getCounterTotal() : BigDecimal.ZERO;
            } else {
                qty = 0;
                price = BigDecimal.ZERO;
                total = BigDecimal.ZERO;
            }

            html.append(tableRow(sku, desc, qty, price, total));
        }
        html.append("</table>");
        return html.toString();
    }

    private BigDecimal calcTotal(List<OfferItem> items, PriceSource source) {
        return items.stream()
                .map(i -> i.getTotalPrice() != null ? i.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcCounterTotal(List<OfferItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (OfferItem item : items) {
            String status = item.getItemStatus();
            if ("Accept".equals(status)) {
                total = total.add(item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO);
            } else if ("Counter".equals(status) || "Finalize".equals(status)) {
                total = total.add(item.getCounterTotal() != null ? item.getCounterTotal() : BigDecimal.ZERO);
            }
        }
        return total;
    }

    private String buildDescription(Device d) {
        if (d == null) return "";
        List<String> parts = new ArrayList<>();
        if (d.getModel() != null) parts.add(d.getModel().getDisplayName());
        if (d.getCarrier() != null) parts.add(d.getCarrier().getDisplayName());
        if (d.getCapacity() != null) parts.add(d.getCapacity().getDisplayName());
        if (d.getColor() != null) parts.add(d.getColor().getDisplayName());
        return String.join(", ", parts);
    }

    private String wrapEmailBody(String title, String buyerName, String companyName,
                                  String offerId, String tableHtml, BigDecimal total,
                                  String extraHtml) {
        String totalHtml = "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"padding-top: 24px;\">"
                + "<tr><td align=\"right\" style=\"padding: 8px 0; font-size: 16px; color: #514F4E; white-space: nowrap;\">"
                + "<span style=\"font-weight: 400; margin-right: 8px; font-family: Trebuchet MS, Helvetica, sans-serif;\">Total:</span>"
                + "<span style=\"font-weight: 700; font-family: Trebuchet MS, Helvetica, sans-serif;\">$"
                + CURRENCY_FMT.format(total) + "</span></td></tr></table>";

        String footer = "<div style=\"display: flex; padding: 16px 0px; flex-direction: column; align-items: center; gap: 16px; align-self: stretch;\">"
                + "<span style=\"color: #000; text-align: center; font-family: Trebuchet MS, Helvetica, sans-serif; font-size: 12px; font-style: normal; font-weight: 400; line-height: 140%;\">"
                + "This email is sent by ecoATM, LLC, 10121 Barnes Canyon Rd, San Diego, CA 92121. "
                + "This is an automated message, please do not reply. Copyright 2025 - ecoATM, LLC. All Rights Reserved."
                + "</span></div>";

        return tableHtml + totalHtml + (extraHtml != null ? extraHtml : "") + footer;
    }

    private void sendOrLog(String templateName, String to, String subject, String body) {
        if (emailEnabled) {
            // TODO: Wire to JavaMailSender or external email API when SMTP is configured
            log.info("[EMAIL-SEND] template={}, to={}, subject={}", templateName, to, subject);
        } else {
            log.info("[EMAIL-STUB] template={}, to={}, subject={} (email sending disabled — set pws.email.enabled=true)",
                    templateName, to, subject);
        }
    }

    private Map<Long, Device> loadDeviceMap(List<OfferItem> items) {
        Set<Long> ids = items.stream()
                .map(OfferItem::getDeviceId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) return Map.of();
        return deviceRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Device::getId, d -> d));
    }

    // ---- HTML constants (matching Mendix inline styles) ----

    private static final String TH_STYLE = "text-align: left; font-weight: 400; font-size: 12px; color: #6F6F6F; "
            + "padding: 8px; font-family: Trebuchet MS, Helvetica, sans-serif;";

    private static final String TABLE_HEADER = "<table style=\"border-collapse: collapse; width: 100%;\">"
            + "<tr style=\"border-bottom: 1px solid #B7B5B5\">"
            + "<th style=\"" + TH_STYLE + "width: 20%;\">SKU</th>"
            + "<th style=\"" + TH_STYLE + "width: 41%;\">Description</th>"
            + "<th style=\"" + TH_STYLE + "text-align: center; width: 13%;\">Ordered Qty</th>"
            + "<th style=\"" + TH_STYLE + "text-align: right; width: 13%;\">Unit Price</th>"
            + "<th style=\"" + TH_STYLE + "text-align: right; width: 13%;\">Total Price</th>"
            + "</tr>";

    private static final String TD_STYLE = "color: #514F4E; padding: 8px; font-size: 14px; font-style: normal; "
            + "font-weight: 400; line-height: 22px; font-family: Trebuchet MS, Helvetica, sans-serif;";

    private String tableRow(String sku, String desc, int qty, BigDecimal price, BigDecimal total) {
        return "<tr style=\"border-bottom: 1px solid #B7B5B5\">"
                + "<td style=\"" + TD_STYLE + "text-align: left;\">" + sku + "</td>"
                + "<td style=\"" + TD_STYLE + "text-align: left; font-weight: 700;\">" + desc + "</td>"
                + "<td style=\"" + TD_STYLE + "text-align: center;\">" + qty + "</td>"
                + "<td style=\"" + TD_STYLE + "text-align: right;\">$" + CURRENCY_FMT.format(price) + "</td>"
                + "<td style=\"" + TD_STYLE + "text-align: right;\">$" + CURRENCY_FMT.format(total) + "</td>"
                + "</tr>";
    }
}
