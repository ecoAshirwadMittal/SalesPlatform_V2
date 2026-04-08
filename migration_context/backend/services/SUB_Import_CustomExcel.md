# Microflow Detailed Specification: SUB_Import_CustomExcel

### 📥 Inputs (Parameters)
- **$CustomExcel_2** (Type: XLSReport.CustomExcel)
- **$MxTemplate** (Type: XLSReport.MxTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$CustomExcel_2 != empty`
   ➔ **If [true]:**
      1. **Update **$MxTemplate**
      - Set **MxTemplate_CustomExcel** = `$CustomExcel_2`**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.