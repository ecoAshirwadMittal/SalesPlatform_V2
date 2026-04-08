# Microflow Analysis: ACT_OpenInventoryAuctionOverview_ForSelectedAuction

### Requirements (Inputs):
- **$AggInventoryHelper** (A record of type: AuctionUI.AggInventoryHelper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SelectedAuction****
2. **Retrieve
      - Store the result in a new variable called **$Week****
3. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction_HelperList****
4. **Take the list **$SchedulingAuction_HelperList**, perform a [FindByExpression] where: { $currentObject/AuctionUI.SchedulingAuction_Helper_Auction=$SelectedAuction }, and call the result **$SchedulingAuction_Helper_Auction****
5. **Decision:** "helper not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Run another process: "AuctionUI.ACT_OpenInventoryOverviewForSelectedAuction"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
