# Microflow Analysis: ACT_Delete_Auction

### Requirements (Inputs):
- **$AggInventoryHelper** (A record of type: AuctionUI.AggInventoryHelper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Week****
2. **Retrieve
      - Store the result in a new variable called **$Auction****
3. **Delete**
4. **Execute Database Query** ⚠️ *(This step has a safety catch if it fails)*
5. **Execute Database Query** ⚠️ *(This step has a safety catch if it fails)*
6. **Retrieve
      - Store the result in a new variable called **$AggregatedInventoryList****
7. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggInventoryHelper.HasAuction] to: "false
"
      - Change [AuctionUI.AggInventoryHelper.HasInventory] to: "$AggregatedInventoryList!=empty
"
      - Change [AuctionUI.AggInventoryHelper.HasSchedule] to: "false
"**
8. **Show Page**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
