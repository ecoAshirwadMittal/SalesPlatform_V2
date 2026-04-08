# Microflow Detailed Specification: MB_NewPubKey

### ⚙️ Execution Flow (Logic Steps)
1. **Create **Encryption.PGPCertificate** (Result: **$NewCertificate**)
      - Set **CertificateType** = `Encryption.CertificateType.PublicKey`**
2. **Maps to Page: **Encryption.PublicKey_NewEdit****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.