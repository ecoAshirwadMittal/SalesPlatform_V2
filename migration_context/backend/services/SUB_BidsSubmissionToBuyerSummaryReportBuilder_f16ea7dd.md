# Microflow Analysis: SUB_BidsSubmissionToBuyerSummaryReportBuilder

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)
- **$BidDataList** (A record of type: EcoATM_Buyer.BidData)
- **$BuyerBidSummaryReport** (A record of type: AuctionUI.BuyerBidSummaryReport)

### Execution Steps:
1. **Log Message**
2. **Create Variable**
3. **Retrieve
      - Store the result in a new variable called **$Week****
4. **Search the Database for **AuctionUI.Week** using filter: { [
  (
    WeekNumber = $Week/WeekNumber -1
    or WeekID = $Week/WeekID -1
  )
] } (Call this list **$LastWeek**)**
5. **Search the Database for **AuctionUI.BuyerCode** using filter: { [
  (
    Code = $BuyerBidSummaryReport/BuyerCode
  )
] } (Call this list **$BuyerCode**)**
6. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [AuctionUI.AggregatedInventory_Week = $Week
and
(
EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true)
and
(EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BuyerCode = $BuyerCode
or
EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/Code = $BuyerCode/Code
)and
EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/Sanitized = true] } (Call this list **$AggregatedInventoryListTupleNew**)**
7. **Aggregate List
      - Store the result in a new variable called **$LotsBidNew****
8. **Aggregate List
      - Store the result in a new variable called **$UnitsBidNew****
9. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [AuctionUI.AggregatedInventory_Week = $LastWeek
and
(
EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true)
and
(EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BuyerCode = $BuyerCode
or
EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/Code = $BuyerCode/Code
)and
EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/Sanitized = true] } (Call this list **$AggregatedInventoryListTupleOld**)**
10. **Search the Database for **EcoATM_Buyer.BidData** using filter: { [(
EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week= $LastWeek)
and
EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true]
[Sanitized = true]
[EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode = $BuyerCode
or
EcoATM_Buyer.BidData_BuyerCode = $BuyerCode] } (Call this list **$BidDataListPrimaryOld**)**
11. **Aggregate List
      - Store the result in a new variable called **$LotsBidOld****
12. **Aggregate List
      - Store the result in a new variable called **$UnitsBidOld****
13. **Create Variable**
14. **Create Variable**
15. **Create Variable**
16. **Update the **$undefined** (Object):
      - Change [AuctionUI.BuyerBidSummaryReport.LotsBid1] to: "$LotsBidNew"
      - Change [AuctionUI.BuyerBidSummaryReport.LotsBid2] to: "$LotsBidOld"
      - Change [AuctionUI.BuyerBidSummaryReport.UnitsBid1] to: "$UnitsBidNewTotal"
      - Change [AuctionUI.BuyerBidSummaryReport.UnitsBid2] to: "$UnitsBidOld"
      - Change [AuctionUI.BuyerBidSummaryReport.UpOrDownLotsBid] to: "$LotsBidArrow"
      - Change [AuctionUI.BuyerBidSummaryReport.UpOrDownUnitsBid] to: "$UnitsBidArrow"
      - Change [AuctionUI.BuyerBidSummaryReport.Auction1] to: "$Week/AuctionUI.Auction_Week/AuctionUI.Auction/AuctionTitle"
      - Change [AuctionUI.BuyerBidSummaryReport.Auction2] to: "$LastWeek/AuctionUI.Auction_Week/AuctionUI.Auction/AuctionTitle"
      - Change [AuctionUI.BuyerBidSummaryReport_Week] to: "$Week"**
17. **Permanently save **$undefined** to the database.**
18. **Create Variable**
19. **Log Message**
20. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
