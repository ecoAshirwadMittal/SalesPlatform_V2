# Microflow Detailed Specification: DS_Create_OrderDetailsHelper

### ⚙️ Execution Flow (Logic Steps)
1. **Create **EcoATM_PWS.OrderDetailHelper** (Result: **$NewOrderDetailHelper**)
      - Set **OrderDetailDataGridSource** = `EcoATM_PWS.ENUM_OrderDetailsDataGridSource.BySKU`**
2. 🏁 **END:** Return `$NewOrderDetailHelper`

**Final Result:** This process concludes by returning a [Object] value.