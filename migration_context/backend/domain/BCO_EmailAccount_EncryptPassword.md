# Microflow Detailed Specification: BCO_EmailAccount_EncryptPassword

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Encryption.Encrypt** (Result: **$encryptedPassword**)**
2. **Update **$EmailAccount**
      - Set **Password** = `$encryptedPassword`**
3. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.