# Microflow Detailed Specification: IVK_ImportTemplateDocument

### 📥 Inputs (Parameters)
- **$TemplateDocument** (Type: ExcelImporter.TemplateDocument)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **TemplateDocument_Template** via Association from **$TemplateDocument** (Result: **$Template**)**
2. 🔀 **DECISION:** `$Template != empty`
   ➔ **If [true]:**
      1. **JavaCallAction**
      2. **Show Message (Information): `The import is finished. {1} records have been imported.`**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Warning): `No template selected for this file`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.