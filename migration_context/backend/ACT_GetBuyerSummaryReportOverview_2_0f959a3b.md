# Microflow Analysis: ACT_GetBuyerSummaryReportOverview_2

### Execution Steps:
1. **Search the Database for **AuctionUI.Auction** using filter: { [
  (
    AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/Status = 'Started'
or
    AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/Status = 'Closed'
  )
] } (Call this list **$Auction_Active**)**
2. **Decision:** "Auction DOES NOT Exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Search the Database for **AuctionUI.Auction** using filter: { [AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/Status = 'Closed'] } (Call this list **$Auction_Closed**)**
4. **Show Message**
5. **Show Home Page**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
