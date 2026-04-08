# Microflow Detailed Specification: SUB_Group_Delete

### 📥 Inputs (Parameters)
- **$GroupId** (Type: Variable)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔀 **DECISION:** `$Authorization != empty`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `false`
   ➔ **If [true]:**
      1. **Create Variable **$Location** = `@MicrosoftGraph.GraphLocation+'/groups/'+$GroupId`**
      2. **Call Microflow **MicrosoftGraph.DELETE** (Result: **$Response**)**
      3. 🔀 **DECISION:** `Successful?`
         ➔ **If [false]:**
            1. **ImportXml**
            2. **LogMessage**
            3. 🏁 **END:** Return `false`
         ➔ **If [true]:**
            1. **LogMessage**
            2. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.