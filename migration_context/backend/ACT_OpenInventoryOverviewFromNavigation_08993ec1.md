# Microflow Analysis: ACT_OpenInventoryOverviewFromNavigation

### Requirements (Inputs):
- **$AggInventoryHelper** (A record of type: AuctionUI.AggInventoryHelper)
- **$SchedulingAuctionHelper_2** (A record of type: AuctionUI.SchedulingAuction_Helper)

### Execution Steps:
1. **Search the Database for **AuctionUI.Auction** using filter: { [AuctionUI.Auction_Week/EcoATM_MDM.Week !=empty] } (Call this list **$Auction_MostRecent**)**
2. **Decision:** "Auction exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Retrieve
      - Store the result in a new variable called **$Week****
4. **Retrieve
      - Store the result in a new variable called **$AggregatedInventoryList****
5. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
6. **Run another process: "AuctionUI.ACT_LoadScheduleAuction_Helper"
      - Store the result in a new variable called **$SchedulingAuction_Helper****
7. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggInventoryHelper.HasAuction] to: "$Week/AuctionUI.Auction_Week/AuctionUI.Auction!=empty
"
      - Change [AuctionUI.AggInventoryHelper.HasInventory] to: "$AggregatedInventoryList!=empty
"
      - Change [AuctionUI.AggInventoryHelper_Week] to: "$Week
"
      - Change [AuctionUI.AggInventoryHelper.HasSchedule] to: "$SchedulingAuctionList!=empty
"
      - Change [AuctionUI.AggInventoryHelper.HasAuctionBeenTriggered] to: "$Week/AuctionUI.Auction_Week/AuctionUI.Auction!=empty
"
      - Change [AuctionUI.AggInventoryHelper_Auction] to: "$Auction_MostRecent
"**
8. **Show Page**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
