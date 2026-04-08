# Microflow Detailed Specification: SUB_User_ListDirectReports

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)
- **$UserIdOrUserPrincipalName_Optional** (Type: Variable)
- **$Parameters_Optional** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔀 **DECISION:** `$Authorization != empty`
   ➔ **If [true]:**
      1. **Create Variable **$Location** = `@MicrosoftGraph.GraphLocation+'/me/directReports'`**
      2. 🔀 **DECISION:** `Check if "UserIdOrUserPrincipalName_Optional" is not empty`
         ➔ **If [true]:**
            1. **Update Variable **$Location** = `@MicrosoftGraph.GraphLocation+'/users/'+$UserIdOrUserPrincipalName_Optional+'/directReports'`**
            2. 🔀 **DECISION:** `Check if "$Parameters_Optional" is not empty`
               ➔ **If [true]:**
                  1. **Update Variable **$Location** = `@MicrosoftGraph.GraphLocation+'/users/'+$UserIdOrUserPrincipalName_Optional+'?'+$Parameters_Optional`**
                  2. **Call Microflow **MicrosoftGraph.GET** (Result: **$Response**)**
                  3. 🔀 **DECISION:** `Successful?`
                     ➔ **If [true]:**
                        1. **ImportXml**
                        2. **Retrieve related **Value_Response** via Association from **$Request** (Result: **$Value**)**
                        3. **Retrieve related **User_Value** via Association from **$Value** (Result: **$UserList**)**
                        4. **LogMessage**
                        5. 🏁 **END:** Return `$UserList`
                     ➔ **If [false]:**
                        1. **ImportXml**
                        2. **LogMessage**
                        3. 🏁 **END:** Return `empty`
               ➔ **If [false]:**
                  1. **Call Microflow **MicrosoftGraph.GET** (Result: **$Response**)**
                  2. 🔀 **DECISION:** `Successful?`
                     ➔ **If [true]:**
                        1. **ImportXml**
                        2. **Retrieve related **Value_Response** via Association from **$Request** (Result: **$Value**)**
                        3. **Retrieve related **User_Value** via Association from **$Value** (Result: **$UserList**)**
                        4. **LogMessage**
                        5. 🏁 **END:** Return `$UserList`
                     ➔ **If [false]:**
                        1. **ImportXml**
                        2. **LogMessage**
                        3. 🏁 **END:** Return `empty`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `Check if "$Parameters_Optional" is not empty`
               ➔ **If [true]:**
                  1. **Update Variable **$Location** = `@MicrosoftGraph.GraphLocation+'/me?'+$Parameters_Optional`**
                  2. **Call Microflow **MicrosoftGraph.GET** (Result: **$Response**)**
                  3. 🔀 **DECISION:** `Successful?`
                     ➔ **If [true]:**
                        1. **ImportXml**
                        2. **Retrieve related **Value_Response** via Association from **$Request** (Result: **$Value**)**
                        3. **Retrieve related **User_Value** via Association from **$Value** (Result: **$UserList**)**
                        4. **LogMessage**
                        5. 🏁 **END:** Return `$UserList`
                     ➔ **If [false]:**
                        1. **ImportXml**
                        2. **LogMessage**
                        3. 🏁 **END:** Return `empty`
               ➔ **If [false]:**
                  1. **Call Microflow **MicrosoftGraph.GET** (Result: **$Response**)**
                  2. 🔀 **DECISION:** `Successful?`
                     ➔ **If [true]:**
                        1. **ImportXml**
                        2. **Retrieve related **Value_Response** via Association from **$Request** (Result: **$Value**)**
                        3. **Retrieve related **User_Value** via Association from **$Value** (Result: **$UserList**)**
                        4. **LogMessage**
                        5. 🏁 **END:** Return `$UserList`
                     ➔ **If [false]:**
                        1. **ImportXml**
                        2. **LogMessage**
                        3. 🏁 **END:** Return `empty`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [List] value.