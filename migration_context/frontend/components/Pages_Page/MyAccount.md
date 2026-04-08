# Page: MyAccount

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📝 **ReferenceSelector**: referenceSelector1
  - ⚡ **Button**: Change password [Style: Default]
    ↳ [acti] → **Microflow**: `Administration.ShowMyPasswordForm`
    ↳ [acti] → **Save Changes**
    ↳ [acti] → **Cancel Changes**
