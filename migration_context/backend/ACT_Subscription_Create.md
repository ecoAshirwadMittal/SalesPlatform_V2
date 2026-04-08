# Microflow Detailed Specification: ACT_Subscription_Create

### 📥 Inputs (Parameters)
- **$Subscription** (Type: MicrosoftGraph.Subscription)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **MicrosoftGraph.SUB_Authorization_GetActive** (Result: **$Authorization**)**
2. **Call Microflow **MicrosoftGraph.SUB_Subscription_Create** (Result: **$Subscribed**)**
3. 🔀 **DECISION:** `Check if "$Subscription/_Id" exists`
   ➔ **If [true]:**
      1. **Commit/Save **$Subscription** to Database**
      2. **Close current page/popup**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Retrieve related **Error_Authorization** via Association from **$Authorization** (Result: **$ErrorList**)**
      2. **List Operation: **Head** on **$undefined** (Result: **$NewError**)**
      3. **Show Message (Warning): `Something went wrong, please review the logs for more information: {1}`**
      4. **Delete**
      5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.