# Page: PublicKey_NewEdit

**Allowed Roles:** Encryption.User

**Layout:** `Encryption.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🔤 **Text**: "Reference"
  - 🔤 **Text**: "Email"
  - 🔤 **Text**: "Name"
  - 🔤 **Text**: "File"
    ↳ [acti] → **Microflow**: `Encryption.MB_SaveCertificate`
    ↳ [acti] → **Cancel Changes**
