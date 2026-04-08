# Microflow Detailed Specification: SUB_Group_GetById

### 📥 Inputs (Parameters)
- **$GroupId** (Type: Variable)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔀 **DECISION:** `$Authorization != empty`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `empty`
   ➔ **If [true]:**
      1. **Create Variable **$Location** = `@MicrosoftGraph.GraphLocation+'/groups/'+$GroupId`**
      2. **Call Microflow **MicrosoftGraph.GET** (Result: **$Response**)**
      3. 🔀 **DECISION:** `Successful?`
         ➔ **If [false]:**
            1. **ImportXml**
            2. **LogMessage**
            3. 🏁 **END:** Return `empty`
         ➔ **If [true]:**
            1. **ImportXml**
            2. **LogMessage**
            3. 🏁 **END:** Return `$Group`

**Final Result:** This process concludes by returning a [Object] value.