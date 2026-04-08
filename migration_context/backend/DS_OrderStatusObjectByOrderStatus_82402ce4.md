# Microflow Analysis: DS_OrderStatusObjectByOrderStatus

### Requirements (Inputs):
- **$OfferAndOrdersView** (A record of type: EcoATM_PWS.OfferAndOrdersView)

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.OrderStatus** using filter: { [ExternalStatusText=$OfferAndOrdersView/OrderStatus] } (Call this list **$OrderStatus**)**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
