# Page: ImportXML_Upload

**Allowed Roles:** ExcelImporter.Configurator

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - ⚡ **Button**: Import [Style: Success]
    ↳ [acti] → **Microflow**: `ExcelImporter.ExcelTemplate_ImportFromXml`
    ↳ [acti] → **Cancel Changes**
