# Microflow Analysis: ACT_Open_CurrentWeek_InventoryAuctionOverview

### Execution Steps:
1. **Run another process: "AuctionUI.DS_GetOrCreateAggregatedInventoryHelper"
      - Store the result in a new variable called **$AggInventoryHelper****
2. **Run another process: "AuctionUI.GetOrCreateSchedulingAuctionHelper"
      - Store the result in a new variable called **$SchedulingAuction_Helper****
3. **Run another process: "AuctionUI.ACT_OpenInventoryOverviewFromNavigation"**
4. **Run another process: "AuctionUI.ACT_OpenInventoryOverviewForSelectedAuction"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
