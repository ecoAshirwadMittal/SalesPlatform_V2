# Microflow Detailed Specification: DS_OrderStatusObjectByOrderStatus

### 📥 Inputs (Parameters)
- **$OfferAndOrdersView** (Type: EcoATM_PWS.OfferAndOrdersView)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.OrderStatus** Filter: `[ExternalStatusText=$OfferAndOrdersView/OrderStatus]` (Result: **$OrderStatus**)**
2. 🏁 **END:** Return `$OrderStatus`

**Final Result:** This process concludes by returning a [Object] value.