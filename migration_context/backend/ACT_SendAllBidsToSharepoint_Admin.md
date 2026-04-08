# Microflow Detailed Specification: ACT_SendAllBidsToSharepoint_Admin

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **Retrieve related **BidRound_SchedulingAuction** via Association from **$SchedulingAuction** (Result: **$BidRoundList**)**
3. **List Operation: **Filter** on **$undefined** where `true` (Result: **$NewBidRoundList_submitted**)**
4. 🔄 **LOOP:** For each **$IteratorBidRound** in **$NewBidRoundList_submitted**
   │ 1. **Retrieve related **BidRound_BuyerCode** via Association from **$IteratorBidRound** (Result: **$BuyerCode**)**
   │ 2. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_TransferBuyerCodeBidsToSharepoint****
   └─ **End Loop**
5. **LogMessage**
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.