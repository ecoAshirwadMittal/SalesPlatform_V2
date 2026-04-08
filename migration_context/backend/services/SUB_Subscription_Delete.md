# Microflow Detailed Specification: SUB_Subscription_Delete

### 📥 Inputs (Parameters)
- **$Subscription** (Type: MicrosoftGraph.Subscription)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔀 **DECISION:** `$Authorization != empty`
   ➔ **If [true]:**
      1. **Create Variable **$Location** = `@MicrosoftGraph.GraphLocation+'/subscriptions/'+$Subscription/_Id`**
      2. **Call Microflow **MicrosoftGraph.DELETE** (Result: **$Response**)**
      3. 🔀 **DECISION:** `Successful?`
         ➔ **If [true]:**
            1. **LogMessage**
            2. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. **Call Microflow **MicrosoftGraph.SUB_Response_ErrorMessage** (Result: **$ErrorMessage**)**
            2. **LogMessage**
            3. 🏁 **END:** Return `false`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.