# Page: ColumnSettings_NewEdit

**Allowed Roles:** XLSReport.Configurator

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🔤 **Text**: "Column number"
  - 🔤 **Text**: "Automatic resize"
  - 📝 **CheckBox**: checkBox1
  - 🔤 **Text**: "Column width (pixels)"
  - ⚡ **Button**: Save [Style: Default]
    ↳ [acti] → **Microflow**: `XLSReport.Settings_Save`
    ↳ [acti] → **Cancel Changes**
