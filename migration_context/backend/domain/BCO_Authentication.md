# Microflow Detailed Specification: BCO_Authentication

### 📥 Inputs (Parameters)
- **$Authentication** (Type: MicrosoftGraph.Authentication)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `startsWith($Authentication/Client_Secret, '{AES')`
   ➔ **If [false]:**
      1. **Call Microflow **Encryption.Encrypt** (Result: **$EncryptedSecret**)**
      2. **Update **$Authentication**
      - Set **Client_Secret** = `$EncryptedSecret`**
      3. 🏁 **END:** Return `true`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.