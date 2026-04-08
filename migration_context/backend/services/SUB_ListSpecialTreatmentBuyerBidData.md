# Microflow Detailed Specification: SUB_ListSpecialTreatmentBuyerBidData

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$Auction** (Type: AuctionUI.Auction)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Retrieve related **QualifiedBuyerCodes_SchedulingAuction** via Association from **$SchedulingAuction** (Result: **$QualifiedBuyerCodesList**)**
3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_BuyerManagement.QualifiedBuyerCodes_BuyerCode/EcoATM_BuyerManagement.BuyerCode = $BuyerCode` (Result: **$QualifiedBuyerCodesList_filtered**)**
4. **List Operation: **Head** on **$undefined** (Result: **$QualifiedBuyerCode**)**
5. 🔀 **DECISION:** `$QualifiedBuyerCode/isSpecialTreatment = true`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. 🏁 **END:** Return `empty`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_BuyerManagement.SUB_CreateBidDataForAllAE** (Result: **$BidDataList_Special**)**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return `$BidDataList_Special`

**Final Result:** This process concludes by returning a [List] value.