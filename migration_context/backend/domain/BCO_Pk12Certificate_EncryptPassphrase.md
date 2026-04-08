# Microflow Detailed Specification: BCO_Pk12Certificate_EncryptPassphrase

### 📥 Inputs (Parameters)
- **$Pk12Certificate** (Type: Email_Connector.Pk12Certificate)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Encryption.Encrypt** (Result: **$encryptedPWD**)**
2. **Update **$Pk12Certificate**
      - Set **Passphrase** = `$encryptedPWD`**
3. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.