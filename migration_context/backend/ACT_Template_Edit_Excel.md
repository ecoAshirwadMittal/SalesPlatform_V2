# Microflow Detailed Specification: ACT_Template_Edit_Excel

### 📥 Inputs (Parameters)
- **$MxTemplate** (Type: XLSReport.MxTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **XLSReport.VAL_Template_Edit_Excel** (Result: **$IsValid**)**
2. 🔀 **DECISION:** `$IsValid`
   ➔ **If [true]:**
      1. **Commit/Save **$MxTemplate** to Database**
      2. **Close current page/popup**
      3. 🏁 **END:** Return `$IsValid`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$IsValid`

**Final Result:** This process concludes by returning a [Boolean] value.