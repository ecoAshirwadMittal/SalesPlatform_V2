# Microflow Analysis: ACT_OpenInventoryOverviewForSelectedAuction

### Requirements (Inputs):
- **$SelectedWeek** (A record of type: EcoATM_MDM.Week)
- **$AggInventoryHelper** (A record of type: AuctionUI.AggInventoryHelper)
- **$SchedulingAuctionHelper_2** (A record of type: AuctionUI.SchedulingAuction_Helper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Auction****
2. **Retrieve
      - Store the result in a new variable called **$AggregatedInventoryList****
3. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
4. **Run another process: "AuctionUI.ACT_LoadScheduleAuction_Helper"
      - Store the result in a new variable called **$SchedulingAuction_Helper****
5. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggInventoryHelper.HasAuction] to: "$Auction!=empty

"
      - Change [AuctionUI.AggInventoryHelper.HasInventory] to: "$AggregatedInventoryList!=empty
"
      - Change [AuctionUI.AggInventoryHelper_Week] to: "$SelectedWeek
"
      - Change [AuctionUI.AggInventoryHelper.HasSchedule] to: "$SchedulingAuctionList!=empty
"
      - Change [AuctionUI.AggInventoryHelper.HasAuctionBeenTriggered] to: "$Auction!=empty
"**
6. **Show Page**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
