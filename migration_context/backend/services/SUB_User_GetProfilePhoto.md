# Microflow Detailed Specification: SUB_User_GetProfilePhoto

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)
- **$UserIdOrUserPrincipalName** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔀 **DECISION:** `$Authorization != empty`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `empty`
   ➔ **If [true]:**
      1. **Create Variable **$Location** = `@MicrosoftGraph.GraphLocation+'/users/'+$UserIdOrUserPrincipalName+'/photo'`**
      2. **Call Microflow **MicrosoftGraph.GET** (Result: **$Response**)**
      3. 🔀 **DECISION:** `Successful?`
         ➔ **If [true]:**
            1. **Call Microflow **Encryption.Decrypt** (Result: **$DecryptedToken**)**
            2. **RestCall**
            3. **ImportXml**
            4. **LogMessage**
            5. 🏁 **END:** Return `$Photo`
         ➔ **If [false]:**
            1. **ImportXml**
            2. **LogMessage**
            3. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.