# Microflow Detailed Specification: SUB_User_GetMyProfile

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Location** = `@MicrosoftGraph.GraphLocation+'/me'`**
2. 🔀 **DECISION:** `$Authorization != empty`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `empty`
   ➔ **If [true]:**
      1. **Call Microflow **MicrosoftGraph.GET** (Result: **$Response**)**
      2. 🔀 **DECISION:** `Successful?`
         ➔ **If [true]:**
            1. **ImportXml**
            2. **LogMessage**
            3. 🏁 **END:** Return `$User`
         ➔ **If [false]:**
            1. **ImportXml**
            2. **LogMessage**
            3. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.