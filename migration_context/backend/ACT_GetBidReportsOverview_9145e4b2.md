# Microflow Analysis: ACT_GetBidReportsOverview

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Search the Database for **AuctionUI.Week** using filter: { [(WeekNumber = $week and Year = $year)] } (Call this list **$ExistingWeek**)**
4. **Decision:** "Week missing?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Create Object
      - Store the result in a new variable called **$CurrentWeek****
6. **Create Object
      - Store the result in a new variable called **$InventoryOverview_Helper_NewWeek****
7. **Show Page**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
