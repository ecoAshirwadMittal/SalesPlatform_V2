# Microflow Detailed Specification: BCO_LDAPConfiguration_EncryptPassword

### 📥 Inputs (Parameters)
- **$LDAPConfiguration** (Type: Email_Connector.LDAPConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$LDAPConfiguration/AuthType = Email_Connector.ENUM_LDAPAuthType.simple`
   ➔ **If [true]:**
      1. **Call Microflow **Encryption.Encrypt** (Result: **$encryptedPWD**)**
      2. **Update **$LDAPConfiguration**
      - Set **LDAPPassword** = `$encryptedPWD`**
      3. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.