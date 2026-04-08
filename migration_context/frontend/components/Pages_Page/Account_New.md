# Page: Account_New

**Allowed Roles:** Administration.Administrator

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📦 **DataView** [Context]
    - 📝 **InputReferenceSetSelector**: referenceSetSelector2
    - 📝 **CheckBox**: checkBox1
    - 📝 **CheckBox**: checkBox2
    - 📝 **ReferenceSelector**: referenceSelector1
    - 📝 **ReferenceSelector**: referenceSelector2
  - ⚡ **Button**: Save [Style: Success]
    ↳ [acti] → **Microflow**: `Administration.SaveNewAccount`
    ↳ [acti] → **Cancel Changes**
