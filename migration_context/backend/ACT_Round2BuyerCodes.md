# Microflow Detailed Specification: ACT_Round2BuyerCodes

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **AuctionUI.BidRoundSelectionFilter**  (Result: **$BidRoundSelectionFilter**)**
3. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[EcoATM_Buyer.BidData_BuyerCode/EcoATM_Buyer.BidData[ (BidAmount > 0) and ( ( TargetPrice = 0 and BidAmount > 0) or ( (TargetPrice != 0) and (BidAmount div TargetPrice >= 1 - $BidRoundSelectionFilter/TargetPercent)) or (TargetPrice - BidAmount <= $BidRoundSelectionFilter/TargetValue) )]/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round = 1]/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionTitle=$Auction/AuctionTitle]` (Result: **$BuyerCodeList**)**
4. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer[AuctionUI.EcoATMDirectUser_Buyer/AuctionUI.EcoATMDirectUser/Name=$currentUser/Name]]` (Result: **$BuyerCodeList_UserAssigned**)**
5. **List Operation: **Intersect** on **$undefined** (Result: **$BuyerCodeList_Intersection**)**
6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
7. 🏁 **END:** Return `$BuyerCodeList_Intersection`

**Final Result:** This process concludes by returning a [List] value.