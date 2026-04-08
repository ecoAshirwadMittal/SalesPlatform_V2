# Microflow Detailed Specification: SUB_User_GetById

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)
- **$UserId** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔀 **DECISION:** `$Authorization != empty`
   ➔ **If [true]:**
      1. **Create Variable **$Location** = `@MicrosoftGraph.GraphLocation+'/users/'+$UserId`**
      2. **Call Microflow **MicrosoftGraph.GET** (Result: **$Response**)**
      3. 🔀 **DECISION:** `Successful?`
         ➔ **If [false]:**
            1. **ImportXml**
            2. **LogMessage**
            3. 🏁 **END:** Return `empty`
         ➔ **If [true]:**
            1. **ImportXml**
            2. **LogMessage**
            3. 🏁 **END:** Return `$User`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.