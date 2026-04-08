# Page: EmailTemplate_CreateAndEdit_Reset

**Allowed Roles:** ForgotPassword.Administrator

**Layout:** `Atlas_Core.Atlas_Default`

## Widget Tree

- 📦 **DataView** [Context]
  - 📦 **DataView** [MF: ForgotPassword.DS_CreateSMTPObject]
    - 📝 **ReferenceSelector**: referenceSelector3
  - 📦 **DataView** [MF: ForgotPassword.DS_CreateLanguageObject]
    - 📝 **ReferenceSelector**: referenceSelector1
    ↳ [acti] → **Microflow**: `ForgotPassword.ACT_ConnectLanguageToTemplate`
    ↳ [acti] → **Cancel Changes**
