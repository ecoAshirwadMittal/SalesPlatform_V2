# Microflow Detailed Specification: MB_GetKeyStore

### 📥 Inputs (Parameters)
- **$SSOConfiguration** (Type: SAML20.SSOConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SSOConfiguration_KeyStore** via Association from **$SSOConfiguration** (Result: **$KeyStore**)**
2. 🔀 **DECISION:** `$KeyStore != empty`
   ➔ **If [true]:**
      1. **Call Microflow **Encryption.Decrypt** (Result: **$DecrptPassword**)**
      2. **Update **$KeyStore** (and Save to DB)
      - Set **Password** = `$DecrptPassword`**
      3. **Maps to Page: **SAML20.KeyStoreUpload****
      4. 🏁 **END:** Return `$KeyStore`
   ➔ **If [false]:**
      1. **Create **SAML20.KeyStore** (Result: **$NewKeyStore**)
      - Set **SSOConfiguration_KeyStore** = `$SSOConfiguration`**
      2. **Maps to Page: **SAML20.KeyStoreUpload****
      3. 🏁 **END:** Return `$NewKeyStore`

**Final Result:** This process concludes by returning a [Object] value.