# Microflow Analysis: SUB_CalculateRound2TargetPrice_Admin

### Requirements (Inputs):
- **$AggInventoryHelper** (A record of type: AuctionUI.AggInventoryHelper)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Retrieve
      - Store the result in a new variable called **$Week****
3. **Retrieve
      - Store the result in a new variable called **$Auction****
4. **Decision:** "auction exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
6. **Take the list **$SchedulingAuctionList**, perform a [Find] where: { 1 }, and call the result **$SchedulingAuction_Round1****
7. **Decision:** "Round1 Closed?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
8. **Show Message**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
