# Microflow Detailed Specification: MB_NewPrivateKey

### ⚙️ Execution Flow (Logic Steps)
1. **Create **Encryption.PGPCertificate** (Result: **$NewCertificate**)
      - Set **CertificateType** = `Encryption.CertificateType.PrivateKey`**
2. **Call Microflow **Encryption.DS_GetAssociatedPubKey** (Result: **$PublicKey**)**
3. **Maps to Page: **Encryption.PrivateKey_NewEdit****
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.