# Microflow Detailed Specification: ACT_Subscription_Delete

### 📥 Inputs (Parameters)
- **$Subscription** (Type: MicrosoftGraph.Subscription)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **MicrosoftGraph.SUB_Subscription_Delete** (Result: **$SubscriptionDeleted**)**
2. 🔀 **DECISION:** `$SubscriptionDeleted = true`
   ➔ **If [true]:**
      1. **Delete**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Retrieve related **Error_Authorization** via Association from **$Authorization** (Result: **$ErrorList**)**
      2. **List Operation: **Head** on **$undefined** (Result: **$NewError**)**
      3. **Show Message (Error): `Subscription could not be deleted: {1}`**
      4. **Delete**
      5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.