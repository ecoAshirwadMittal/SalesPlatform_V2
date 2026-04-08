# Microflow Detailed Specification: ACT_SavePWSConstants

### 📥 Inputs (Parameters)
- **$PWSConstants** (Type: EcoATM_PWS.PWSConstants)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_PWS.VAL_PWSConstants** (Result: **$isValid**)**
2. 🔀 **DECISION:** `$isValid`
   ➔ **If [true]:**
      1. **Commit/Save **$PWSConstants** to Database**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.