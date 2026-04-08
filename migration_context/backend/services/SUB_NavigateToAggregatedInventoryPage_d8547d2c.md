# Microflow Analysis: SUB_NavigateToAggregatedInventoryPage

### Execution Steps:
1. **Run another process: "AuctionUI.DS_GetOrCreateAggregatedInventoryHelper"
      - Store the result in a new variable called **$AggInventoryHelper****
2. **Search the Database for **EcoATM_MDM.Week** using filter: { [WeekEndDateTime > '[%CurrentDateTime%]']
 } (Call this list **$Week**)**
3. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggInventoryHelper_Week] to: "$Week
"**
4. **Run another process: "AuctionUI.SUB_LoadAggregatedInventoryTotals"**
5. **Retrieve
      - Store the result in a new variable called **$AggregatedInventoryList****
6. **Retrieve
      - Store the result in a new variable called **$AuctionExists****
7. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggInventoryHelper.HasInventory] to: "$AggregatedInventoryList!=empty
"
      - Change [AuctionUI.AggInventoryHelper.HasAuction] to: "$AuctionExists!=empty
"
      - Change [AuctionUI.AggInventoryHelper.isCurrentWeek] to: "true
"**
8. **Show Page**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
