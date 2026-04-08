# Microflow Analysis: ACT_GetBuyerBidDetailReportMenu

### Requirements (Inputs):
- **$WeekOld** (A record of type: AuctionUI.Week)
- **$WeekNew** (A record of type: AuctionUI.Week)
- **$BuyerCode** (A record of type: AuctionUI.BuyerCode)

### Execution Steps:
1. **Search the Database for **EcoATM_Buyer.BidData** using filter: { [
(
EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $WeekNew
or
EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $WeekNew
)

and
EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true
] } (Call this list **$BidDataList**)**
2. **Search the Database for **AuctionUI.BuyerBidDetailReport** using filter: { [
  (
    BuyerCode = $BuyerCode/Code
  )
] } (Call this list **$BuyerBidDetailReportListPreExisting**)**
3. **Run another process: "AuctionUI.Sub_BidDataSanitize"
      - Store the result in a new variable called **$BidDataListClean****
4. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [
  (
    AuctionUI.AggregatedInventory_Week = $WeekNew
  )
and
(
EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/BidAmount > 0
and
EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/Code = $BuyerCode/Code
)
] } (Call this list **$AggregatedInventoryListNew**)**
5. **Search the Database for **EcoATM_Buyer.BidData** using filter: { [
(
EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $WeekOld
or
EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $WeekOld
)
and
EcoATM_Buyer.BidData_BuyerCode = $BuyerCode
and
EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true
] } (Call this list **$BidDataListOld**)**
6. **Run another process: "AuctionUI.Sub_BidDataSanitize"
      - Store the result in a new variable called **$BidDataListCleanOld****
7. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [
  (
    AuctionUI.AggregatedInventory_Week = $WeekOld
  )
and
(
EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/BidAmount > 0
and
EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/Code = $BuyerCode/Code
)
] } (Call this list **$AggregatedInventoryListOld**)**
8. **Create List
      - Store the result in a new variable called **$BuyerBidDetailReportHelperList****
9. **Create List
      - Store the result in a new variable called **$BuyerBidDetailReportList****
10. **Create Variable**
11. **Create Variable**
12. **Create Variable**
13. **Create Variable**
14. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
15. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
16. **Create Variable**
17. **Create Variable**
18. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
19. **Create Object
      - Store the result in a new variable called **$NewBuyerBidUnitsLotsHelper****
20. **Permanently save **$undefined** to the database.**
21. **Permanently save **$undefined** to the database.**
22. **Delete**
23. **Show Page**
24. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
