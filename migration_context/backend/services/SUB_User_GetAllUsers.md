# Microflow Detailed Specification: SUB_User_GetAllUsers

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔀 **DECISION:** `$Authorization != empty`
   ➔ **If [true]:**
      1. **Create Variable **$Location** = `@MicrosoftGraph.GraphLocation+'/users'`**
      2. **CreateList**
      3. **Call Microflow **MicrosoftGraph.GET** (Result: **$Response**)**
      4. 🔀 **DECISION:** `Successful?`
         ➔ **If [true]:**
            1. **ImportXml**
            2. **Call Microflow **MicrosoftGraph.SUB_User_ProcessResponse****
            3. 🔀 **DECISION:** `Done?`
               ➔ **If [true]:**
                  1. **LogMessage**
                  2. 🏁 **END:** Return `$UserList`
               ➔ **If [false]:**
                  1. **Update Variable **$Location** = `$GraphResponse/_odata_nextlink`**
                     *(Merging with existing path logic)*
         ➔ **If [false]:**
            1. **ImportXml**
            2. **LogMessage**
            3. 🏁 **END:** Return `$UserList`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [List] value.