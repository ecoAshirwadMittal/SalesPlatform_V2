# Microflow Detailed Specification: SUB_CheckRoundIncluded

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$Buyercode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'CheckRoundIncluded_OpenDashboard'`**
2. **Create Variable **$Description** = `'Check if buyer code is Included for round - BuyerCode: ' + $Buyercode/Code`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **Retrieve related **QualifiedBuyerCodes_SchedulingAuction** via Association from **$SchedulingAuction** (Result: **$QualifiedBuyerCodeList**)**
5. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_BuyerManagement.QualifiedBuyerCodes_BuyerCode/EcoATM_BuyerManagement.BuyerCode = $Buyercode and $currentObject/Included = true` (Result: **$QualifiedBuyerCode**)**
6. **AggregateList**
7. 🔀 **DECISION:** `$CountIncluded > 0`
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
      2. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.