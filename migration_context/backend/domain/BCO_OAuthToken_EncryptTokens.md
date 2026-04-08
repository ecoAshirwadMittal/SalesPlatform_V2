# Microflow Detailed Specification: BCO_OAuthToken_EncryptTokens

### 📥 Inputs (Parameters)
- **$OAuthToken** (Type: Email_Connector.OAuthToken)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Encryption.Encrypt** (Result: **$access_token**)**
2. **Call Microflow **Encryption.Encrypt** (Result: **$refresh_token**)**
3. **Call Microflow **Encryption.Encrypt** (Result: **$id_token**)**
4. **Update **$OAuthToken**
      - Set **access_token** = `$access_token`
      - Set **refresh_token** = `$refresh_token`
      - Set **id_token** = `$id_token`**
5. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.