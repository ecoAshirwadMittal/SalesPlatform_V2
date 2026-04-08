# Page: KeyStoreUpload

**Allowed Roles:** SAML20.Administrator

**Layout:** `MxModelReflection.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🔤 **Text**: "* Encryption settings are ignored when providing your own key store file."
  - ⚡ **Button**: Save [Style: Success]
    ↳ [acti] → **Microflow**: `SAML20.MB_UploadKeyStore`
    ↳ [acti] → **Cancel Changes**
