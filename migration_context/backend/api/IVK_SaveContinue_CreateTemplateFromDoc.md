# Microflow Detailed Specification: IVK_SaveContinue_CreateTemplateFromDoc

### 📥 Inputs (Parameters)
- **$TemplateDocument** (Type: ExcelImporter.TemplateDocument)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **ExcelImporter.Validate_TemplateDocument** (Result: **$isValid**)**
2. 🔀 **DECISION:** `$isValid`
   ➔ **If [true]:**
      1. **Retrieve related **TemplateDocument_Template** via Association from **$TemplateDocument** (Result: **$Template**)**
      2. **Call Microflow **ExcelImporter.SF_Template_CheckNrs** (Result: **$IsValid**)**
      3. 🔀 **DECISION:** `$IsValid`
         ➔ **If [true]:**
            1. **Update **$Template** (and Save to DB)**
            2. **JavaCallAction**
            3. **Delete**
            4. **Close current page/popup**
            5. **Maps to Page: **ExcelImporter.Template_Edit****
            6. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.