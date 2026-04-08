# Page: Template_New_FromDocument

**Allowed Roles:** ExcelImporter.Configurator

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📦 **DataView** [Context]
      ↳ [Change] → **Microflow**: `ExcelImporter.Ch_Template_CheckNrs`
      ↳ [Change] → **Microflow**: `ExcelImporter.Ch_Template_CheckNrs`
      ↳ [Change] → **Microflow**: `ExcelImporter.Ch_Template_CheckNrs`
  - ⚡ **Button**: Save & next [Style: Success]
    ↳ [acti] → **Microflow**: `ExcelImporter.IVK_SaveContinue_CreateTemplateFromDoc`
  - ⚡ **Button**: Cancel [Style: Default]
    ↳ [acti] → **Microflow**: `ExcelImporter.IVK_TemplateDoc_Cancel`
