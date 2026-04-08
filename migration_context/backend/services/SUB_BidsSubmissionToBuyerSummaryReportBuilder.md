# Microflow Detailed Specification: SUB_BidsSubmissionToBuyerSummaryReportBuilder

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$BidDataList** (Type: EcoATM_Buyer.BidData)
- **$BuyerBidSummaryReport** (Type: AuctionUI.BuyerBidSummaryReport)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **Create Variable **$StartTime** = `[%CurrentDateTime%]`**
3. **Retrieve related **BuyerBidSummaryReport_Week** via Association from **$BuyerBidSummaryReport** (Result: **$Week**)**
4. **DB Retrieve **AuctionUI.Week** Filter: `[ ( WeekNumber = $Week/WeekNumber -1 or WeekID = $Week/WeekID -1 ) ]` (Result: **$LastWeek**)**
5. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[ ( Code = $BuyerBidSummaryReport/BuyerCode ) ]` (Result: **$BuyerCode**)**
6. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week = $Week and ( EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true) and (EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BuyerCode = $BuyerCode or EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/Code = $BuyerCode/Code )and EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/Sanitized = true]` (Result: **$AggregatedInventoryListTupleNew**)**
7. **AggregateList**
8. **AggregateList**
9. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week = $LastWeek and ( EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true) and (EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/EcoATM_Buyer.BidData_BuyerCode = $BuyerCode or EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/Code = $BuyerCode/Code )and EcoATM_Buyer.BidData_AggregatedInventory/EcoATM_Buyer.BidData/Sanitized = true]` (Result: **$AggregatedInventoryListTupleOld**)**
10. **DB Retrieve **EcoATM_Buyer.BidData** Filter: `[( EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week= $LastWeek) and EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true] [Sanitized = true] [EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode = $BuyerCode or EcoATM_Buyer.BidData_BuyerCode = $BuyerCode]` (Result: **$BidDataListPrimaryOld**)**
11. **AggregateList**
12. **AggregateList**
13. **Create Variable **$UnitsBidNewTotal** = `$UnitsBidNew + $BuyerBidSummaryReport/UnitsBid1`**
14. **Create Variable **$LotsBidArrow** = `if $LotsBidNew > $LotsBidOld then '⇧' else if $LotsBidNew < $LotsBidOld then '⇩' else ''`**
15. **Create Variable **$UnitsBidArrow** = `if $UnitsBidNewTotal > $UnitsBidOld then '⇧' else if $UnitsBidNewTotal < $UnitsBidOld then '⇩' else ''`**
16. **Update **$BuyerBidSummaryReport**
      - Set **LotsBid1** = `$LotsBidNew`
      - Set **LotsBid2** = `$LotsBidOld`
      - Set **UnitsBid1** = `$UnitsBidNewTotal`
      - Set **UnitsBid2** = `$UnitsBidOld`
      - Set **UpOrDownLotsBid** = `$LotsBidArrow`
      - Set **UpOrDownUnitsBid** = `$UnitsBidArrow`
      - Set **Auction1** = `$Week/AuctionUI.Auction_Week/AuctionUI.Auction/AuctionTitle`
      - Set **Auction2** = `$LastWeek/AuctionUI.Auction_Week/AuctionUI.Auction/AuctionTitle`
      - Set **BuyerBidSummaryReport_Week** = `$Week`**
17. **Commit/Save **$BuyerBidSummaryReport** to Database**
18. **Create Variable **$EndTime** = `[%CurrentDateTime%]`**
19. **LogMessage**
20. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.