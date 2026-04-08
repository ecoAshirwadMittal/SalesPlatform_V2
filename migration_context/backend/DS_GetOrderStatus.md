# Microflow Detailed Specification: DS_GetOrderStatus

### 📥 Inputs (Parameters)
- **$OfferAndOrdersView** (Type: EcoATM_PWS.OfferAndOrdersView)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.OrderStatus** Filter: `[EcoATM_PWS.Order_OrderStatus/EcoATM_PWS.Order/OrderNumber = $OfferAndOrdersView/OrderNumber]` (Result: **$OrderStatus**)**
2. 🏁 **END:** Return `$OrderStatus`

**Final Result:** This process concludes by returning a [Object] value.