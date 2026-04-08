# Microflow Detailed Specification: SUB_Decrypt

### 📥 Inputs (Parameters)
- **$EncryptedString** (Type: Variable)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Encryption.Decrypt** (Result: **$decryptedString**)**
2. 🏁 **END:** Return `$decryptedString`

**Final Result:** This process concludes by returning a [String] value.