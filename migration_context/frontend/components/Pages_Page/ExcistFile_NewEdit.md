# Page: ExcistFile_NewEdit

**Allowed Roles:** XLSReport.Configurator

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🔤 **Text**: "File"
  - 📝 **CheckBox**: checkBox 🔒 [Read-Only] [Style: `display: none;`]
  - ⚡ **Button**: Save [Style: Default]
    ↳ [acti] → **Microflow**: `XLSReport.IVK_ExcistFile_Save`
    ↳ [acti] → **Cancel Changes**
