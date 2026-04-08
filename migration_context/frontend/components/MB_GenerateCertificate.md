# Microflow Detailed Specification: MB_GenerateCertificate

### 📥 Inputs (Parameters)
- **$Certificate_PrivateKey** (Type: Encryption.PGPCertificate)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Encryption.DS_GetAssociatedPubKey** (Result: **$Certificate_PubKey**)**
2. **JavaCallAction**
3. **Call Microflow **Encryption.Encrypt** (Result: **$EncryptedPassword**)**
4. **Update **$Certificate_PrivateKey** (and Save to DB)
      - Set **PassPhrase_Plain** = `empty`
      - Set **PassPhrase_Encrypted** = `$EncryptedPassword`**
5. **Commit/Save **$Certificate_PubKey** to Database**
6. **Close current page/popup**
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.