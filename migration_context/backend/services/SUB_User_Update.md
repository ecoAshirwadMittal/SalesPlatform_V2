# Microflow Detailed Specification: SUB_User_Update

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)
- **$User** (Type: MicrosoftGraph.User)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔀 **DECISION:** `$Authorization != empty`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `false`
   ➔ **If [true]:**
      1. **Create Variable **$Location** = `@MicrosoftGraph.GraphLocation+'/users/'+$User/_Id`**
      2. **ExportXml**
      3. **Call Microflow **MicrosoftGraph.PATCH** (Result: **$Response**)**
      4. 🔀 **DECISION:** `Successful?`
         ➔ **If [true]:**
            1. **LogMessage**
            2. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. **ImportXml**
            2. **LogMessage**
            3. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.