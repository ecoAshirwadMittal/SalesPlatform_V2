# Microflow Detailed Specification: SUB_User_ListGroupMembers

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)
- **$GroupId** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔀 **DECISION:** `$Authorization != empty`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `empty`
   ➔ **If [true]:**
      1. **Create Variable **$Location** = `@MicrosoftGraph.GraphLocation+'/groups/'+$GroupId+'/members'`**
      2. **Call Microflow **MicrosoftGraph.GET** (Result: **$Response**)**
      3. 🔀 **DECISION:** `Successful?`
         ➔ **If [false]:**
            1. **ImportXml**
            2. **LogMessage**
            3. 🏁 **END:** Return `empty`
         ➔ **If [true]:**
            1. **ImportXml**
            2. **Retrieve related **Value_Response** via Association from **$GraphResponse** (Result: **$Value**)**
            3. **Retrieve related **User_Value** via Association from **$Value** (Result: **$UserList**)**
            4. **LogMessage**
            5. 🏁 **END:** Return `$UserList`

**Final Result:** This process concludes by returning a [List] value.