# Microflow Analysis: ACT_UnscheduleAuction

### Requirements (Inputs):
- **$AggInventoryHelper** (A record of type: AuctionUI.AggInventoryHelper)
- **$SchedulingAuction_Helper** (A record of type: AuctionUI.SchedulingAuction_Helper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$CurrentWeek****
2. **Retrieve
      - Store the result in a new variable called **$AggregatedInventoryList****
3. **Retrieve
      - Store the result in a new variable called **$Auction****
4. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
5. **Take the list **$SchedulingAuctionList**, perform a [FilterByExpression] where: { $currentObject/HasRound = true
and
$currentObject/RoundStatus = AuctionUI.enum_SchedulingAuctionStatus.Started }, and call the result **$StartedSchedulingAuctionList****
6. **Decision:** "Auction not started?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Update the **$undefined** (Object):
      - Change [AuctionUI.Auction.AuctionStatus] to: "AuctionUI.enum_SchedulingAuctionStatus.Unscheduled
"
      - **Save:** This change will be saved to the database immediately.**
8. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
9. **Permanently save **$undefined** to the database.**
10. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggInventoryHelper.HasAuction] to: "$Auction!=empty
"
      - Change [AuctionUI.AggInventoryHelper.HasInventory] to: "$AggregatedInventoryList!=empty
"
      - Change [AuctionUI.AggInventoryHelper.HasSchedule] to: "$StartedSchedulingAuctionList!=empty
"
      - Change [AuctionUI.AggInventoryHelper.HasAuctionBeenTriggered] to: "$Auction!=empty
"**
11. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
12. **Decision:** "send auction data to snowflake?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
13. **Run another process: "AuctionUI.SUB_SendAuctionAndSchedulingActionToSnowflake_async"**
14. **Show Page**
15. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
