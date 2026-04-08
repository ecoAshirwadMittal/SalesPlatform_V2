# Microflow Detailed Specification: ACT_Subscription_Update

### 📥 Inputs (Parameters)
- **$Subscription** (Type: MicrosoftGraph.Subscription)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **MicrosoftGraph.SUB_Authorization_GetActive** (Result: **$Authorization**)**
2. **Call Microflow **MicrosoftGraph.SUB_Subscription_Renew** (Result: **$Updated**)**
3. 🔀 **DECISION:** `$Updated`
   ➔ **If [true]:**
      1. **Close current page/popup**
      2. **Update **$Subscription****
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Retrieve related **Error_Authorization** via Association from **$Authorization** (Result: **$ErrorList**)**
      2. **List Operation: **Head** on **$undefined** (Result: **$NewError**)**
      3. **ValidationFeedback**
      4. **Delete**
      5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.