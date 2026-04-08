# Microflow Analysis: ACT_GetBidsReportsPage_2

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Search the Database for **AuctionUI.Week** using filter: { [(WeekNumber = $week and Year = $year)] } (Call this list **$ExistingWeek**)**
4. **Run another process: "AuctionUI.ACT_GetMostRecentAuction"
      - Store the result in a new variable called **$Auction****
5. **Decision:** "Auction Exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Show Message**
7. **Show Home Page**
8. **Decision:** "1 = 2"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
9. **Create Object
      - Store the result in a new variable called **$CurrentWeek****
10. **Create Object
      - Store the result in a new variable called **$NewAuctionsBidReportHeader****
11. **Create Object
      - Store the result in a new variable called **$NewAuctionsBidReportHelper****
12. **Create Object
      - Store the result in a new variable called **$NewAuctionsBidReportTuple****
13. **Create Object
      - Store the result in a new variable called **$NewAuctionsBidReportTupleHelper****
14. **Create Object
      - Store the result in a new variable called **$InventoryOverview_Helper_NewWeek****
15. **Show Page**
16. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
