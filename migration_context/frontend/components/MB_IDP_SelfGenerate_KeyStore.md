# Microflow Detailed Specification: MB_IDP_SelfGenerate_KeyStore

### 📥 Inputs (Parameters)
- **$SSOConfiguration** (Type: SAML20.SSOConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$SSOConfiguration/UseEncryption`
   ➔ **If [true]:**
      1. **Retrieve related **SSOConfiguration_KeyStore** via Association from **$SSOConfiguration** (Result: **$KeyStore**)**
      2. 🔀 **DECISION:** `$KeyStore = empty`
         ➔ **If [true]:**
            1. **DB Retrieve **SAML20.SSOConfiguration** Filter: `[id = $SSOConfiguration]` (Result: **$ReloadSSOConfiguration**)**
            2. **JavaCallAction**
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **JavaCallAction**
            2. 🔀 **DECISION:** `$IsValidKeyPair=true`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$SSOConfiguration/isUploadNewKeyPair = false`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **DB Retrieve **SAML20.SSOConfiguration** Filter: `[id = $SSOConfiguration]` (Result: **$ReloadSSOConfiguration**)**
                        2. **JavaCallAction**
                        3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **JavaCallAction**
                  2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.