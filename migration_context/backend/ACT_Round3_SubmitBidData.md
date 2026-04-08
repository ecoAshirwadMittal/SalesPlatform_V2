# Microflow Detailed Specification: ACT_Round3_SubmitBidData

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$BidRound** (Type: AuctionUI.BidRound)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Call Microflow **AuctionUI.ACT_CreateBidSubmitLog** (Result: **$BidSubmitLog**)**
3. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
4. **Call Microflow **EcoATM_BuyerManagement.ACT_GetSubmittedBidRounds** (Result: **$BidderRouterHelper**)**
5. **Retrieve related **BidRound_BuyerCode** via Association from **$BidRound** (Result: **$BuyerCode**)**
6. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound = $BidRound]` (Result: **$BidDataList**)**
7. **List Operation: **FilterByExpression** on **$undefined** where `($currentObject/BidAmount != empty and $currentObject/BidAmount>0) or ($currentObject/SubmittedBidAmount != empty and $currentObject/SubmittedBidAmount>0)` (Result: **$ExcludeEmptyBidDataList**)**
8. **CreateList**
9. 🔄 **LOOP:** For each **$IteratorBidData** in **$ExcludeEmptyBidDataList**
   │ 1. **Update **$IteratorBidData**
      - Set **SubmittedBidAmount** = `$IteratorBidData/BidAmount`
      - Set **SubmittedBidQuantity** = `$IteratorBidData/BidQuantity`
      - Set **SubmittedDateTime** = `[%CurrentDateTime%]`**
   │ 2. **Add **$$IteratorBidData
** to/from list **$BidDataToCommit****
   └─ **End Loop**
10. **Commit/Save **$BidDataToCommit** to Database**
11. **Update **$BidRound** (and Save to DB)
      - Set **Submitted** = `true`
      - Set **SubmittedDatatime** = `[%CurrentDateTime%]`**
12. **Call Microflow **EcoATM_BuyerManagement.ACT_BidDataDoc_PopulateExcelDoc****
13. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_TransferBuyerCodeBidsToSharepoint****
14. **Call Microflow **AuctionUI.SUB_SendSubmitBidConfirmationEmail****
15. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendBidDataToSnowflake`
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.SUB_SendBidDataToSnowflake****
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.