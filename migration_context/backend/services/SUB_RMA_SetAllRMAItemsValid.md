# Microflow Detailed Specification: SUB_RMA_SetAllRMAItemsValid

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **RMAItem_RMA** via Association from **$RMA** (Result: **$RMAItemList**)**
2. **List Operation: **Find** on **$undefined** where `empty` (Result: **$RMAItem_StatusEmpty**)**
3. 🔀 **DECISION:** `$RMAItem_StatusEmpty!=empty`
   ➔ **If [true]:**
      1. **Update **$RMA**
      - Set **AllRMAItemsValid** = `false`**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$RMA**
      - Set **AllRMAItemsValid** = `true`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.