# Page: PrivateKey_NewEdit

**Allowed Roles:** Encryption.User

**Layout:** `Encryption.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📦 **DataView** [Context]
    - 🔤 **Text**: "Name"
    - 🔤 **Text**: "File"
  - 🔤 **Text**: "Reference"
  - 🔤 **Text**: "Email"
  - 🔤 **Text**: "Name"
  - 🔤 **Text**: "Pass Phrase"
  - 🔤 **Text**: "File"
    ↳ [acti] → **Microflow**: `Encryption.MB_SaveCertificate`
    ↳ [acti] → **Cancel Changes**
