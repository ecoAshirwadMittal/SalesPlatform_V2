# Microflow Detailed Specification: BCO_Authorization

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `Check if "Authorization/Refresh_Token" exists`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `Check if "Authorization/Access_Token" exists`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `true`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `startsWith($Authorization/Access_Token, '{AES')`
               ➔ **If [false]:**
                  1. **Call Microflow **Encryption.Encrypt** (Result: **$EncryptedAccessToken**)**
                  2. **Update **$Authorization**
      - Set **Access_Token** = `$EncryptedAccessToken`**
                  3. 🏁 **END:** Return `true`
               ➔ **If [true]:**
                  1. 🏁 **END:** Return `true`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `startsWith($Authorization/Refresh_Token, '{AES')`
         ➔ **If [false]:**
            1. **Call Microflow **Encryption.Encrypt** (Result: **$EncryptedRefreshToken**)**
            2. **Update **$Authorization**
      - Set **Refresh_Token** = `$EncryptedRefreshToken`**
            3. 🔀 **DECISION:** `Check if "Authorization/Access_Token" exists`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `true`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `startsWith($Authorization/Access_Token, '{AES')`
                     ➔ **If [false]:**
                        1. **Call Microflow **Encryption.Encrypt** (Result: **$EncryptedAccessToken**)**
                        2. **Update **$Authorization**
      - Set **Access_Token** = `$EncryptedAccessToken`**
                        3. 🏁 **END:** Return `true`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `true`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `Check if "Authorization/Access_Token" exists`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `true`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `startsWith($Authorization/Access_Token, '{AES')`
                     ➔ **If [false]:**
                        1. **Call Microflow **Encryption.Encrypt** (Result: **$EncryptedAccessToken**)**
                        2. **Update **$Authorization**
      - Set **Access_Token** = `$EncryptedAccessToken`**
                        3. 🏁 **END:** Return `true`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.