# Microflow Analysis: DS_GetOrderStatus

### Requirements (Inputs):
- **$OfferAndOrdersView** (A record of type: EcoATM_PWS.OfferAndOrdersView)

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.OrderStatus** using filter: { [EcoATM_PWS.Order_OrderStatus/EcoATM_PWS.Order/OrderNumber = $OfferAndOrdersView/OrderNumber] } (Call this list **$OrderStatus**)**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
