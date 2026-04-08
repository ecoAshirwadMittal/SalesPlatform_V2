# Microflow Analysis: ACT_Round2AggregatedInventory

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$SchedulingAuction_Round2** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **EcoATM_BuyerManagement.QualifiedBuyerCodes** using filter: { [EcoATM_BuyerManagement.QualifiedBuyerCodes_SchedulingAuction = $SchedulingAuction_Round2
and
EcoATM_BuyerManagement.QualifiedBuyerCodes_BuyerCode/EcoATM_BuyerManagement.BuyerCode = $BuyerCode]
 } (Call this list **$QualifiedBuyerCode**)**
3. **Decision:** "?Included"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Search the Database for **AuctionUI.BidRoundSelectionFilter** using filter: { [Round=2]
 } (Call this list **$BidRoundSelectionFilter**)**
5. **Run another process: "AuctionUI.SUB_ListBuyerCodeAggregatedInventory"
      - Store the result in a new variable called **$AggregatedInventoryList_Round1Bids****
6. **Search the Database for **AuctionUI.BidData** using filter: { [
AuctionUI.BidData_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code=$BuyerCode/Code 
and 
AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week/EcoATM_MDM.Week/AuctionUI.Auction_Week/AuctionUI.Auction=$Auction
] } (Call this list **$AllRounds_BidDataList**)**
7. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BuyerCode = $BuyerCode]
[AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round=1]/AuctionUI.SchedulingAuction_Auction=$Auction]
[BidAmount>0]
 } (Call this list **$BidDataList_Round1**)**
8. **Create List
      - Store the result in a new variable called **$Round2AggregatedInventoryList****
9. **Create Variable**
10. **Decision:** "Show All Inventory?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
11. **Change Variable**
12. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData[
AuctionUI.BidData_BuyerCode = $BuyerCode
and (BidAmount > 0)
and ( ( TargetPrice = 0 and BidAmount > 0)
or ( (TargetPrice != 0) and (BidAmount div TargetPrice >= 1 - $BidRoundSelectionFilter/TargetPercent))
or (TargetPrice - BidAmount <= $BidRoundSelectionFilter/TargetValue) )]/AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round = 1]/AuctionUI.SchedulingAuction_Auction=$Auction]
 } (Call this list **$AggregatedInventoryList_NonDW**)**
13. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
14. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
15. **Decision:** "? has qualifying bid"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
16. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
