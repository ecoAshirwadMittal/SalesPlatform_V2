# Microflow Detailed Specification: IVK_TemplateEdit

### 📥 Inputs (Parameters)
- **$MxTemplate** (Type: XLSReport.MxTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$MxTemplate/DocumentType`
   ➔ **If [XLS]:**
      1. **Maps to Page: **XLSReport.Template_Edit_Excel****
      2. 🏁 **END:** Return empty
   ➔ **If [(empty)]:**
      1. 🏁 **END:** Return empty
   ➔ **If [XLSX]:**
      1. **Maps to Page: **XLSReport.Template_Edit_Excel****
      2. 🏁 **END:** Return empty
   ➔ **If [CSV]:**
      1. **Maps to Page: **XLSReport.Template_Edit_CSV****
      2. 🏁 **END:** Return empty
   ➔ **If [XLSM]:**
      1. **Maps to Page: **XLSReport.Template_Edit_Excel****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.