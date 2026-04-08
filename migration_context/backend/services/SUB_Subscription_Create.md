# Microflow Detailed Specification: SUB_Subscription_Create

### 📥 Inputs (Parameters)
- **$Subscription** (Type: MicrosoftGraph.Subscription)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔀 **DECISION:** `$Authorization != empty`
   ➔ **If [true]:**
      1. **Create Variable **$Location** = `@MicrosoftGraph.GraphLocation+'/subscriptions'`**
      2. **ExportXml**
      3. **Call Microflow **MicrosoftGraph.POST** (Result: **$Response**)**
      4. 🔀 **DECISION:** `Successful?`
         ➔ **If [true]:**
            1. **ImportXml**
            2. **Update **$Subscription** (and Save to DB)
      - Set **_Id** = `$Subscription_Created/_Id`
      - Set **Subscription_Authorization** = `$Authorization`**
            3. **Delete**
            4. **LogMessage**
            5. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. **Call Microflow **MicrosoftGraph.SUB_Response_ErrorMessage** (Result: **$ErrorMessage**)**
            2. **LogMessage**
            3. 🏁 **END:** Return `false`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.