# Microflow Detailed Specification: MB_SaveCertificate

### 📥 Inputs (Parameters)
- **$Certificate** (Type: Encryption.PGPCertificate)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Certificate/CertificateType`
   ➔ **If [PrivateKey]:**
      1. 🔀 **DECISION:** `$Certificate/PassPhrase_Plain != empty and $Certificate/PassPhrase_Plain != ''`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$Certificate/PassPhrase_Encrypted != empty and $Certificate/PassPhrase_Encrypted != ''`
               ➔ **If [true]:**
                  1. **JavaCallAction**
                  2. 🔀 **DECISION:** `$Certificate/EmailAddress != empty and $Certificate/EmailAddress != ''`
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Certificate/Reference != empty and $Certificate/Reference != ''`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Call Microflow **Encryption.DS_GetAssociatedPubKey** (Result: **$PublicKey**)**
                              2. **Update **$PublicKey** (and Save to DB)
      - Set **Reference** = `$Certificate/Reference`
      - Set **EmailAddress** = `$Certificate/EmailAddress`**
                              3. **Commit/Save **$Certificate** to Database**
                              4. **Close current page/popup**
                              5. **Show Message (Information): `The certificate has been saved`**
                              6. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Call Microflow **Encryption.Encrypt** (Result: **$EncryptedPassword**)**
            2. **Update **$Certificate**
      - Set **PassPhrase_Plain** = `empty`
      - Set **PassPhrase_Encrypted** = `$EncryptedPassword`**
            3. 🔀 **DECISION:** `$Certificate/PassPhrase_Encrypted != empty and $Certificate/PassPhrase_Encrypted != ''`
               ➔ **If [true]:**
                  1. **JavaCallAction**
                  2. 🔀 **DECISION:** `$Certificate/EmailAddress != empty and $Certificate/EmailAddress != ''`
                     ➔ **If [false]:**
                        1. **ValidationFeedback**
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Certificate/Reference != empty and $Certificate/Reference != ''`
                           ➔ **If [false]:**
                              1. **ValidationFeedback**
                              2. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Call Microflow **Encryption.DS_GetAssociatedPubKey** (Result: **$PublicKey**)**
                              2. **Update **$PublicKey** (and Save to DB)
      - Set **Reference** = `$Certificate/Reference`
      - Set **EmailAddress** = `$Certificate/EmailAddress`**
                              3. **Commit/Save **$Certificate** to Database**
                              4. **Close current page/popup**
                              5. **Show Message (Information): `The certificate has been saved`**
                              6. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **ValidationFeedback**
                  2. 🏁 **END:** Return empty
   ➔ **If [PublicKey]:**
      1. 🔀 **DECISION:** `$Certificate/Reference != empty and $Certificate/Reference != ''`
         ➔ **If [false]:**
            1. **ValidationFeedback**
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Call Microflow **Encryption.DS_GetAssociatedPubKey** (Result: **$PublicKey**)**
            2. **Update **$PublicKey** (and Save to DB)
      - Set **Reference** = `$Certificate/Reference`
      - Set **EmailAddress** = `$Certificate/EmailAddress`**
            3. **Commit/Save **$Certificate** to Database**
            4. **Close current page/popup**
            5. **Show Message (Information): `The certificate has been saved`**
            6. 🏁 **END:** Return empty
   ➔ **If [(empty)]:**
      1. **Show Message (Error): `Invalid certificate configuration!`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.