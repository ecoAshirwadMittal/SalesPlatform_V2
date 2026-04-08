# Microflow Detailed Specification: SUB_CheckLastWeekBidDataExists

### 📥 Inputs (Parameters)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Last_Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code=$NP_BuyerCodeSelect_Helper/Code and AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week=$Last_Week and AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted=true]` (Result: **$BidData**)**
3. **LogMessage**
4. 🏁 **END:** Return `$BidData`

**Final Result:** This process concludes by returning a [Object] value.