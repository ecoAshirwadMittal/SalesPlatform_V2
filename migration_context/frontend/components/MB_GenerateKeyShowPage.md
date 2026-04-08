# Microflow Detailed Specification: MB_GenerateKeyShowPage

### ⚙️ Execution Flow (Logic Steps)
1. **Create **Encryption.PGPCertificate** (Result: **$NewCertificate**)
      - Set **CertificateType** = `Encryption.CertificateType.PrivateKey`**
2. **Maps to Page: **Encryption.Certificate_Generate****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.