# Microflow Detailed Specification: MB_UploadKeyStore

### 📥 Inputs (Parameters)
- **$KeyStore** (Type: SAML20.KeyStore)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SSOConfiguration_KeyStore** via Association from **$KeyStore** (Result: **$SSOConfiguration**)**
2. **Create Variable **$EncryptionKeyLength** = `$SSOConfiguration/EncryptionKeyLength`**
3. **Create Variable **$PlainPassword** = `$KeyStore/Password`**
4. **Call Microflow **Encryption.Encrypt** (Result: **$EncryptedPassword**)**
5. **Update **$KeyStore** (and Save to DB)
      - Set **Password** = `$EncryptedPassword`**
6. **JavaCallAction**
7. 🔀 **DECISION:** `$isValidAliasName`
   ➔ **If [true]:**
      1. **Update **$KeyStore** (and Save to DB)
      - Set **LastChangedOn** = `[%CurrentDateTime%]`**
      2. **Close current page/popup**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$KeyStore** (and Save to DB)
      - Set **Password** = `$PlainPassword`**
      2. **Show Message (Error): `The keystore could not be uploaded. Please check the password, the key alias or other content. The SAML module will use a self-generated keypair if no keypair is succesfully uploaded.`**
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.