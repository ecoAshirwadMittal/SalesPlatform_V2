# Microflow Detailed Specification: SUB_User_GetDelta

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)
- **$DeltaQuery** (Type: MicrosoftGraph.DeltaQuery)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔀 **DECISION:** `$Authorization != empty`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `empty`
   ➔ **If [true]:**
      1. **Create Variable **$Location** = `if $DeltaQuery != empty and $DeltaQuery/_odata_deltalink != empty then $DeltaQuery/_odata_deltalink else @MicrosoftGraph.GraphLocation+'/users/delta'`**
      2. **CreateList**
      3. **Call Microflow **MicrosoftGraph.GET** (Result: **$Response**)**
      4. 🔀 **DECISION:** `Successful?`
         ➔ **If [true]:**
            1. **ImportXml**
            2. **Call Microflow **MicrosoftGraph.SUB_User_ProcessResponse****
            3. 🔀 **DECISION:** `Done?`
               ➔ **If [true]:**
                  1. **Update **$DeltaQuery** (and Save to DB)
      - Set **_odata_deltalink** = `$GraphResponse/_odata_deltalink`
      - Set **Resource** = `MicrosoftGraph.ENUM_Resource.User`**
                  2. **LogMessage**
                  3. 🏁 **END:** Return `$UserList`
               ➔ **If [false]:**
                  1. **Update Variable **$Location** = `$GraphResponse/_odata_nextlink`**
                     *(Merging with existing path logic)*
         ➔ **If [false]:**
            1. **ImportXml**
            2. **LogMessage**
            3. 🏁 **END:** Return `$UserList`

**Final Result:** This process concludes by returning a [List] value.