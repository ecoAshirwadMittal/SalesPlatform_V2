# Microflow Detailed Specification: DS_GetOrCreateOrderHistoryHelper

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_PWS.DS_BuyerCodeBySession** (Result: **$BuyerCode**)**
2. **Retrieve related **OrderHistoryHelper_Session** via Association from **$currentSession** (Result: **$OrderHistoryHelperList**)**
3. 🔀 **DECISION:** `$OrderHistoryHelperList = empty`
   ➔ **If [true]:**
      1. **Create **EcoATM_PWS.OrderHistoryHelper** (Result: **$NewOrderHistoryHelper**)
      - Set **RecentDateCutoff** = `addDays([%CurrentDateTime%], -7)`
      - Set **CurrentTab** = `EcoATM_PWS.ENUM_OrderHistoryTab.All`
      - Set **OrderHistoryHelper_Session** = `$currentSession`**
      2. **Call Microflow **EcoATM_PWS.SUB_CalculateOrderHistoryTabTotals****
      3. 🏁 **END:** Return `$NewOrderHistoryHelper`
   ➔ **If [false]:**
      1. **List Operation: **Sort** on **$undefined** sorted by: changedDate (Descending) (Result: **$OrderHistoryHelperList_Sorted**)**
      2. **List Operation: **Head** on **$undefined** (Result: **$OrderHistoryHelper**)**
      3. **Call Microflow **EcoATM_PWS.SUB_CalculateOrderHistoryTabTotals****
      4. 🏁 **END:** Return `$OrderHistoryHelper`

**Final Result:** This process concludes by returning a [Object] value.