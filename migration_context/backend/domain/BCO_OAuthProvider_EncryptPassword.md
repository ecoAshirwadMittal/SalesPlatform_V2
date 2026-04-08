# Microflow Detailed Specification: BCO_OAuthProvider_EncryptPassword

### 📥 Inputs (Parameters)
- **$OAuthProvider** (Type: Email_Connector.OAuthProvider)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Encryption.Encrypt** (Result: **$EncClientSecret**)**
2. **Update **$OAuthProvider**
      - Set **ClientSecret** = `$EncClientSecret`**
3. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.