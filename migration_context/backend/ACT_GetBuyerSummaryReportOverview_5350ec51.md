# Microflow Analysis: ACT_GetBuyerSummaryReportOverview

### Execution Steps:
1. **Search the Database for **AuctionUI.Auction** using filter: { [
  (
    AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/RoundStatus = 'Started'
or
    AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/RoundStatus = 'Closed'
  )
] } (Call this list **$Auction_Active**)**
2. **Decision:** "Auction DOES NOT Exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Retrieve
      - Store the result in a new variable called **$CurrentAuction****
4. **Search the Database for **AuctionUI.Week** using filter: { [
  (
    WeekNumber = $CurrentAuction/WeekNumber -1
    or WeekID = $CurrentAuction/WeekID -1
  )
] } (Call this list **$PreviousAuction**)**
5. **Search the Database for **AuctionUI.BuyerCode** using filter: { [
  (
    BuyerCodeType != empty
  )
and
(
EcoATM_Buyer.BidData_BuyerCode/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $CurrentAuction
or 
EcoATM_Buyer.BidData_BuyerCode/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $CurrentAuction
or
AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $CurrentAuction
or
AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound/EcoATM_Buyer.BidData_BidRound/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $CurrentAuction
)
and
(AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound/Submitted = true
or
EcoATM_Buyer.BidData_BuyerCode/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true
)
] } (Call this list **$BuyerCodeListNew**)**
6. **Search the Database for **AuctionUI.BuyerCode** using filter: { Show everything } (Call this list **$BuyerCodeListALL**)**
7. **Search the Database for **AuctionUI.BuyerCode** using filter: { [
  (
    BuyerCodeType != empty
  )
and
(
EcoATM_Buyer.BidData_BuyerCode/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $PreviousAuction
or
EcoATM_Buyer.BidData_BuyerCode/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $PreviousAuction
or
AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $PreviousAuction
or
AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound/EcoATM_Buyer.BidData_BidRound/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week = $PreviousAuction
)
] } (Call this list **$BuyerCodeListOld**)**
8. **Create List
      - Store the result in a new variable called **$BuyerBidSummaryReportList****
9. **Create List
      - Store the result in a new variable called **$BuyerBidSummaryReportHelperList****
10. **Search the Database for **EcoATM_Buyer.BidData** using filter: { [(EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week= $CurrentAuction
)
and
EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true] } (Call this list **$BidDataListPrimaryNew**)**
11. **Run another process: "AuctionUI.Sub_BidDataSanitize"
      - Store the result in a new variable called **$BidDataListCleanNew****
12. **Search the Database for **EcoATM_Buyer.BidData** using filter: { [(
EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week= $PreviousAuction)
and
EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true] } (Call this list **$BidDataListPrimaryOld**)**
13. **Run another process: "AuctionUI.Sub_BidDataSanitize"
      - Store the result in a new variable called **$BidDataListCleanOld****
14. **Search the Database for **AuctionUI.BuyerBidSummaryReport** using filter: { [
  (
    AuctionUI.BuyerBidSummaryReport_Week = $CurrentAuction
  )
] } (Call this list **$BuyerBidSummaryReportListExisting**)**
15. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { 
[AuctionUI.AggregatedInventory_Week = $PreviousAuction
and
(EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData
or
EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true)
] } (Call this list **$AggregatedInventoryListOld_1**)**
16. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { 
[AuctionUI.AggregatedInventory_Week = $CurrentAuction
and
(EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData
or
EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true)
] } (Call this list **$AggregatedInventoryListNew_1**)**
17. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
18. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
19. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
20. **Permanently save **$undefined** to the database.**
21. **Permanently save **$undefined** to the database.**
22. **Delete**
23. **Show Page**
24. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
