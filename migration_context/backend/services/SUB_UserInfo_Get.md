# Microflow Detailed Specification: SUB_UserInfo_Get

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Location** = `$Authorization/MicrosoftGraph.Authorization_Authentication/MicrosoftGraph.Authentication/Userinfo_Endpoint`**
2. **Call Microflow **MicrosoftGraph.GET** (Result: **$Response**)**
3. 🔀 **DECISION:** `Successful?`
   ➔ **If [true]:**
      1. **ImportXml**
      2. **LogMessage**
      3. 🏁 **END:** Return `$UserInfo`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.