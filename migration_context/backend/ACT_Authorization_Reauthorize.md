# Microflow Detailed Specification: ACT_Authorization_Reauthorize

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **MicrosoftGraph.SUB_Authorization_Reauthorize** (Result: **$Reauthorized**)**
2. 🔀 **DECISION:** `$Reauthorized= true`
   ➔ **If [false]:**
      1. **Update **$Authorization****
      2. **Show Message (Warning): `New access token request failed`**
      3. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$Authorization/AdminConsent`
         ➔ **If [false]:**
            1. **Call Microflow **MicrosoftGraph.SUB_UserInfo_Get** (Result: **$Variable**)**
            2. **Update **$Authorization****
            3. **Show Message (Information): `New access token requested successfully`**
            4. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Update **$Authorization****
            2. **Show Message (Information): `New access token requested successfully`**
            3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.