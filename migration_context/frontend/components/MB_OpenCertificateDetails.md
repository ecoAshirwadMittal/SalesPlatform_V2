# Microflow Detailed Specification: MB_OpenCertificateDetails

### 📥 Inputs (Parameters)
- **$Certificate** (Type: Encryption.PGPCertificate)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Certificate/CertificateType`
   ➔ **If [(empty)]:**
      1. **Show Message (Error): `Invalid certificate configuration! The certificate must have a type`**
      2. 🏁 **END:** Return empty
   ➔ **If [PublicKey]:**
      1. **Maps to Page: **Encryption.PublicKey_NewEdit****
      2. 🏁 **END:** Return empty
   ➔ **If [PrivateKey]:**
      1. **Maps to Page: **Encryption.PrivateKey_NewEdit****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.