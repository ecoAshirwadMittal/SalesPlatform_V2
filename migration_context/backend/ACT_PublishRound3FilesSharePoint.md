# Microflow Detailed Specification: ACT_PublishRound3FilesSharePoint

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.DS_GetRoundThreeBuyers** (Result: **$BuyerCodeList**)**
2. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
3. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/Round=3` (Result: **$Round3SchedulingAuction**)**
4. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
   │ 1. **Call Microflow **EcoATM_BuyerManagement.ACT_BidDataDoc_PopulateExcelDoc_PreRound3****
   └─ **End Loop**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.