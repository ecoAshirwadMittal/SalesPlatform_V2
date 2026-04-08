# Microflow Detailed Specification: SUB_GetBidDownload_Helper_Depricated_10_28_24

### 📥 Inputs (Parameters)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_Caching.NP_BuyerCodeSelect_Helper)
- **$ClickedRound** (Type: AuctionUI.ClickedRound)
- **$NewBidDataDoc** (Type: AuctionUI.BidDataDoc)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **Call Microflow **AuctionUI.ACT_GetMostRecentAuction** (Result: **$Auction**)**
3. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
4. **List Operation: **Filter** on **$undefined** where `$ClickedRound/Round` (Result: **$NewSchedulingAuctionList**)**
5. **List Operation: **Head** on **$undefined** (Result: **$CurrentSchedulingAuction**)**
6. **Retrieve related **BidRound_SchedulingAuction** via Association from **$CurrentSchedulingAuction** (Result: **$BidRoundList**)**
7. **List Operation: **Filter** on **$undefined** where `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode` (Result: **$CurrentBidRoundList**)**
8. **List Operation: **Head** on **$undefined** (Result: **$NewBidRound**)**
9. **Update **$NP_BuyerCodeSelect_Helper**
      - Set **NP_BuyerCodeSelect_Helper_BidRound** = `$NewBidRound`**
10. **Call Microflow **EcoATM_Buyer.SUB_CreateBidDownload_Helper_Depricated_10_28_24** (Result: **$BidDownload_HelperList**)**
11. **LogMessage**
12. 🏁 **END:** Return `$BidDownload_HelperList`

**Final Result:** This process concludes by returning a [List] value.