# Microflow Detailed Specification: DS_TemplateCSVSheet

### 📥 Inputs (Parameters)
- **$MxTemplate** (Type: XLSReport.MxTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **XLSReport.MxSheet** Filter: `[XLSReport.MxSheet_Template = $MxTemplate]` (Result: **$MxSheet**)**
2. 🔀 **DECISION:** `$MxSheet != empty`
   ➔ **If [false]:**
      1. **Create **XLSReport.MxSheet** (Result: **$NewMxSheet**)
      - Set **MxSheet_Template** = `$MxTemplate`**
      2. 🏁 **END:** Return `$NewMxSheet`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$MxSheet`

**Final Result:** This process concludes by returning a [Object] value.