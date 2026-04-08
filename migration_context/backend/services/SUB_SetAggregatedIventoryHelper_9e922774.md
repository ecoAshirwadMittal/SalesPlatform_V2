# Microflow Analysis: SUB_SetAggregatedIventoryHelper

### Requirements (Inputs):
- **$AggInventoryHelper** (A record of type: AuctionUI.AggInventoryHelper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Week****
2. **Run another process: "AuctionUI.SUB_LoadAggregatedInventoryTotals"**
3. **Retrieve
      - Store the result in a new variable called **$AggregatedInventoryList****
4. **Search the Database for **AuctionUI.Auction** using filter: { [AuctionUI.Auction_Week=$Week]
 } (Call this list **$Auction**)**
5. **Search the Database for **EcoATM_MDM.Week** using filter: { [WeekEndDateTime > '[%CurrentDateTime%]']
 } (Call this list **$CurrentWeek**)**
6. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggInventoryHelper.HasInventory] to: "$AggregatedInventoryList!=empty
"
      - Change [AuctionUI.AggInventoryHelper.HasAuction] to: "$Auction!=empty
"
      - Change [AuctionUI.AggInventoryHelper.isCurrentWeek] to: "$CurrentWeek=$Week
"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
