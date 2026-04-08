# Microflow Detailed Specification: SUB_ImportMxTemplate

### 📥 Inputs (Parameters)
- **$CustomExcel** (Type: XLSReport.CustomExcel)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$CustomExcel/HasContents`
   ➔ **If [true]:**
      1. **ImportXml**
      2. **Call Microflow **XLSReport.SUB_Import_Mxtemplate** (Result: **$MxTemplate_2**)**
      3. **Close current page/popup**
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **ValidationFeedback**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.