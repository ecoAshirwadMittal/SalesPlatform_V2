# Microflow Analysis: DS_GetShipmentDetailsForOrder

### Requirements (Inputs):
- **$OfferAndOrdersView** (A record of type: EcoATM_PWS.OfferAndOrdersView)

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.ShipmentDetail** using filter: { [EcoATM_PWS.ShipmentDetail_Order/EcoATM_PWS.Order/OrderNumber = $OfferAndOrdersView/OrderNumber] } (Call this list **$ShipmentDetailList**)**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
