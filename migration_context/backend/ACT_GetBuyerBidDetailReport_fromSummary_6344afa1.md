# Microflow Analysis: ACT_GetBuyerBidDetailReport_fromSummary

### Requirements (Inputs):
- **$BuyerCode_String** (A record of type: Object)

### Execution Steps:
1. **Close Form**
2. **Run another process: "Custom_Logging.SUB_Log_Info"**
3. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [
  (
    Code = $BuyerCode_String
  )
] } (Call this list **$BuyerCode**)**
4. **Search the Database for **AuctionUI.Auction** using filter: { Show everything } (Call this list **$LastTwoAuctions**)**
5. **Take the list **$LastTwoAuctions**, perform a [Head], and call the result **$FirstAuction****
6. **Take the list **$LastTwoAuctions**, perform a [FindByExpression] where: { $currentObject!=$FirstAuction }, and call the result **$SecondAuction****
7. **Retrieve
      - Store the result in a new variable called **$FirstAuctionSchedulingAuctionList****
8. **Retrieve
      - Store the result in a new variable called **$SecondAuctionSchedulingAuctionList****
9. **Take the list **$FirstAuctionSchedulingAuctionList**, perform a [Head], and call the result **$FirstAuctionSchedule****
10. **Take the list **$SecondAuctionSchedulingAuctionList**, perform a [Head], and call the result **$SecondAuctionSchedule****
11. **Create Variable**
12. **Java Action Call
      - Store the result in a new variable called **$SummaryResult**** ⚠️ *(This step has a safety catch if it fails)*
13. **Decision:** "list not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
14. **Aggregate List
      - Store the result in a new variable called **$UnitsBid1****
15. **Aggregate List
      - Store the result in a new variable called **$UnitsBid2****
16. **Take the list **$SummaryResult**, perform a [FilterByExpression] where: { if ($currentObject/Quantity1!=empty and $currentObject/Bid1!=empty)
then
($currentObject/Quantity1>0 and $currentObject/Bid1>0)
else
false }, and call the result **$List_LotsBid1****
17. **Aggregate List
      - Store the result in a new variable called **$LotsBid1****
18. **Take the list **$SummaryResult**, perform a [FilterByExpression] where: { if ($currentObject/Quantity2!=empty and $currentObject/Bid2!=empty)
then
($currentObject/Quantity2>0 and $currentObject/Bid2>0)
else
false }, and call the result **$List_LotsBid2****
19. **Aggregate List
      - Store the result in a new variable called **$LotsBid2****
20. **Create Object
      - Store the result in a new variable called **$NewBuyerBidSummaryReportHelper****
21. **Show Page**
22. **Run another process: "Custom_Logging.SUB_Log_Info"**
23. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
