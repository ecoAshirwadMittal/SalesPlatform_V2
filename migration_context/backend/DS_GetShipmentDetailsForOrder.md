# Microflow Detailed Specification: DS_GetShipmentDetailsForOrder

### 📥 Inputs (Parameters)
- **$OfferAndOrdersView** (Type: EcoATM_PWS.OfferAndOrdersView)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.ShipmentDetail** Filter: `[EcoATM_PWS.ShipmentDetail_Order/EcoATM_PWS.Order/OrderNumber = $OfferAndOrdersView/OrderNumber]` (Result: **$ShipmentDetailList**)**
2. 🏁 **END:** Return `$ShipmentDetailList`

**Final Result:** This process concludes by returning a [List] value.