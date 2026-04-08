# Microflow Analysis: SUB_CreateBidDataForAllAE

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$Auction** (A record of type: AuctionUI.Auction)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [AuctionUI.SchedulingAuction_Auction = $Auction
and Round = $SchedulingAuction/Round - 1] } (Call this list **$SchedulingAuction_PriorRound**)**
3. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BidRound/AuctionUI.BidRound[AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction_PriorRound and AuctionUI.BidRound_BuyerCode = $BuyerCode]] } (Call this list **$BidDataList_PriorRound**)**
4. **Create Variable**
5. **Run another process: "EcoATM_BuyerManagement.SUB_ListAggregateInventoryByBuyerCodeType"
      - Store the result in a new variable called **$AggregatedInventoryList**** ⚠️ *(This step has a safety catch if it fails)*
6. **Create List
      - Store the result in a new variable called **$BidDataList****
7. **Run another process: "EcoATM_BuyerManagement.SUB_GetBidRoundBySAandCode"
      - Store the result in a new variable called **$NewBidRound****
8. **Run another process: "AuctionUI.ACT_BidDataDoc_GetOrCreate"
      - Store the result in a new variable called **$BidDataDoc****
9. **Update the **$undefined** (Object):
      - Change [AuctionUI.BidRound_BidDataDoc] to: "$BidDataDoc
"
      - **Save:** This change will be saved to the database immediately.**
10. **Search the Database for **AuctionUI.BidDataTotalQuantityConfig** using filter: { Show everything } (Call this list **$BidDataTotalQuantityConfigList**)**
11. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
12. **Run another process: "EcoATM_BuyerManagement.SUB_UpdateBlankBidDataWithProrRoundBidData"**
13. **Permanently save **$undefined** to the database.**
14. **Update the **$undefined** (Object):
      - **Save:** This change will be saved to the database immediately.**
15. **Permanently save **$undefined** to the database.**
16. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
17. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
