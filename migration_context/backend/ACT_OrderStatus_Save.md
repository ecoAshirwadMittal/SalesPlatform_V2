# Microflow Detailed Specification: ACT_OrderStatus_Save

### 📥 Inputs (Parameters)
- **$OrderStatus** (Type: EcoATM_PWS.OrderStatus)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_PWS.VAL_OrderStatus_IsValide** (Result: **$IsValide**)**
2. 🔀 **DECISION:** `$IsValide`
   ➔ **If [true]:**
      1. **Commit/Save **$OrderStatus** to Database**
      2. **Call Microflow **Custom_Logging.SUB_Log_Info****
      3. **Close current page/popup**
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.