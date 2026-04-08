# Microflow Detailed Specification: ACT_OrderDetails_Export

### 📥 Inputs (Parameters)
- **$OrderDetailHelper** (Type: EcoATM_PWS.OrderDetailHelper)
- **$OfferAndOrdersView** (Type: EcoATM_PWS.OfferAndOrdersView)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$OrderDetailHelper/OrderDetailDataGridSource= EcoATM_PWS.ENUM_OrderDetailsDataGridSource.BySKU or $OrderDetailHelper/OrderDetailDataGridSource = empty`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_PWS.SUB_OrderDetails_Export_BySKU****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **EcoATM_PWS.SUB_OrderDetails_Export_ByDevice****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.