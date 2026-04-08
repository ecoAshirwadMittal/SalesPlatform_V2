# Microflow Detailed Specification: SUB_Authorization_Reauthorize

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Authorization != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$Authorization/AdminConsent`
         ➔ **If [false]:**
            1. **Call Microflow **MicrosoftGraph.SUB_Authorization_RefreshAccessToken** (Result: **$Successful**)**
            2. 🏁 **END:** Return `$Successful`
         ➔ **If [true]:**
            1. **Call Microflow **MicrosoftGraph.SUB_Authorization_GetAccessToken_ClientCredentials** (Result: **$Successful_CC**)**
            2. 🏁 **END:** Return `$Successful_CC`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.