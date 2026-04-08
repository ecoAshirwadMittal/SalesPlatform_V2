# Microflow Detailed Specification: DS_GetAssociatedPubKey

### 📥 Inputs (Parameters)
- **$Certificate** (Type: Encryption.PGPCertificate)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SecretKey_PublicKey** via Association from **$Certificate** (Result: **$PublicKey**)**
2. 🔀 **DECISION:** `$PublicKey != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$PublicKey`
   ➔ **If [false]:**
      1. **Create **Encryption.PGPCertificate** (Result: **$NewPublicKey**)
      - Set **CertificateType** = `Encryption.CertificateType.PublicKey`
      - Set **Reference** = `$Certificate/Reference`
      - Set **EmailAddress** = `$Certificate/EmailAddress`**
      2. **Update **$Certificate** (and Save to DB)
      - Set **SecretKey_PublicKey** = `$NewPublicKey`**
      3. 🏁 **END:** Return `$NewPublicKey`

**Final Result:** This process concludes by returning a [Object] value.