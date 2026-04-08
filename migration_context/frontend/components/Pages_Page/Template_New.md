# Page: Template_New

**Allowed Roles:** ExcelImporter.Configurator

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - ⚡ **Button**: Save & Next [Style: Success]
    ↳ [acti] → **Microflow**: `ExcelImporter.IVK_SaveNewTemplate`
  - ⚡ **Button**: Save & Generate Columns [Style: Success]
    ↳ [acti] → **Microflow**: `ExcelImporter.IVK_SaveNewTemplate_CreateColumns`
  - ⚡ **Button**: Cancel [Style: Default]
    ↳ [acti] → **Microflow**: `ExcelImporter.IVK_CancelTemplate`
