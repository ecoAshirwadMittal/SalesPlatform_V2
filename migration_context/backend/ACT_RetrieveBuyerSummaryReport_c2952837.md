# Microflow Analysis: ACT_RetrieveBuyerSummaryReport

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Run another process: "EcoATM_Reports.SUB_RetrieveLatestSummaryReportBySession"
      - Store the result in a new variable called **$BuyerBidSummaryReportHelper****
3. **Decision:** "is report not valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
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
12. **Create Variable**
13. **Java Action Call
      - Store the result in a new variable called **$SummaryResult**** ⚠️ *(This step has a safety catch if it fails)*
14. **Retrieve
      - Store the result in a new variable called **$Week****
15. **Create Object
      - Store the result in a new variable called **$NewBuyerBidSummaryReportHelper****
16. **Show Page**
17. **Run another process: "Custom_Logging.SUB_Log_Info"**
18. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
