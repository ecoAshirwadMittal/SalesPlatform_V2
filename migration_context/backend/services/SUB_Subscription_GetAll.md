# Microflow Detailed Specification: SUB_Subscription_GetAll

### 📥 Inputs (Parameters)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Location** = `@MicrosoftGraph.GraphLocation+'/subscriptions'`**
2. **Call Microflow **MicrosoftGraph.GET** (Result: **$Response**)**
3. 🔀 **DECISION:** `Successful?`
   ➔ **If [true]:**
      1. **ImportXml**
      2. **AggregateList**
      3. **LogMessage**
      4. 🏁 **END:** Return `$SubscriptionList`
   ➔ **If [false]:**
      1. **ImportXml**
      2. **LogMessage**
      3. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [List] value.