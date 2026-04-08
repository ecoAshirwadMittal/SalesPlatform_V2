# Microflow Detailed Specification: IVK_UploadExcistFile

### 📥 Inputs (Parameters)
- **$MxTemplate** (Type: XLSReport.MxTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$MxTemplate/XLSReport.MxTemplate_CustomExcel != empty`
   ➔ **If [true]:**
      1. **Retrieve related **MxTemplate_CustomExcel** via Association from **$MxTemplate** (Result: **$ExcistExcel**)**
      2. **Maps to Page: **XLSReport.ExcistFile_NewEdit****
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Create **XLSReport.CustomExcel** (Result: **$NewExcistExcel**)**
      2. **Update **$MxTemplate** (and Save to DB)
      - Set **MxTemplate_CustomExcel** = `$NewExcistExcel`**
      3. **Maps to Page: **XLSReport.ExcistFile_NewEdit****
      4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.