# Microflow Detailed Specification: ACT_Round2BuyerCodes_SalesRep

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.BidRoundSelectionFilter**  (Result: **$BidRoundSelectionFilter**)**
2. **DB Retrieve **AuctionUI.BuyerCode**  (Result: **$BuyerCodeList_all**)**
3. **DB Retrieve **EcoATM_Buyer.BidData**  (Result: **$BidData_all**)**
4. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[EcoATM_Buyer.BidData_BuyerCode/EcoATM_Buyer.BidData[ (BidAmount > 0) and ( ( TargetPrice = 0 and BidAmount > 0) or ( (TargetPrice != 0) and (BidAmount div TargetPrice >= 1 - $BidRoundSelectionFilter/TargetPercent)) or (TargetPrice - BidAmount <= $BidRoundSelectionFilter/TargetValue) )]/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round = 1]/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionTitle=$Auction/AuctionTitle]` (Result: **$BuyerCodeList**)**
5. 🏁 **END:** Return `$BuyerCodeList`

**Final Result:** This process concludes by returning a [List] value.