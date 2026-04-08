# Microflow Analysis: ACT_OrderDetails_Export

### Requirements (Inputs):
- **$OrderDetailHelper** (A record of type: EcoATM_PWS.OrderDetailHelper)
- **$OfferAndOrdersView** (A record of type: EcoATM_PWS.OfferAndOrdersView)

### Execution Steps:
1. **Decision:** "OrderDetailsExport By SKU?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Run another process: "EcoATM_PWS.SUB_OrderDetails_Export_BySKU"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
