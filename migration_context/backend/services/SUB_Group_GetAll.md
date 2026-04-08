# Microflow Detailed Specification: SUB_Group_GetAll

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔀 **DECISION:** `$Authorization != empty`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `empty`
   ➔ **If [true]:**
      1. **Create Variable **$Location** = `@MicrosoftGraph.GraphLocation+'/groups'`**
      2. **Call Microflow **MicrosoftGraph.GET** (Result: **$Response**)**
      3. 🔀 **DECISION:** `Successful?`
         ➔ **If [true]:**
            1. **ImportXml**
            2. **Retrieve related **Value_Response** via Association from **$GraphResponse** (Result: **$Value**)**
            3. **Retrieve related **Group_Value** via Association from **$Value** (Result: **$GroupList**)**
            4. **LogMessage**
            5. 🏁 **END:** Return `$GroupList`
         ➔ **If [false]:**
            1. **ImportXml**
            2. **LogMessage**
            3. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [List] value.