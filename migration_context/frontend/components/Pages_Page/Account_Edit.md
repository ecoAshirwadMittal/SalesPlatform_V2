# Page: Account_Edit

**Allowed Roles:** Administration.Administrator

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🔤 **Text**: "Mendix AppCloud users are provisioned by the AppCloudServices module, any changes made in this form might get overwritten." [Class: `alert alert-warning` | Style: `width:100%;`] 👁️ (If IsLocalUser is true/false)
  - 📝 **InputReferenceSetSelector**: referenceSetSelector2
  - 📝 **CheckBox**: checkBox1
  - 📝 **CheckBox**: checkBox2
  - 📝 **ReferenceSelector**: referenceSelector1
  - 📝 **ReferenceSelector**: referenceSelector2
  - ⚡ **Button**: Change password [Style: Default] 👁️ (If IsLocalUser is true/false)
    ↳ [acti] → **Microflow**: `Administration.ShowPasswordForm`
    ↳ [acti] → **Save Changes**
    ↳ [acti] → **Cancel Changes**
