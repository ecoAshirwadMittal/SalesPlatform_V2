# Microflow Analysis: SUB_GetOrCreateBidRound

### Requirements (Inputs):
- **$BidRoundList** (A record of type: AuctionUI.BidRound)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$Week** (A record of type: EcoATM_MDM.Week)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Take the list **$BidRoundList**, perform a [FindByExpression] where: { $currentObject/AuctionUI.BidRound_BuyerCode = $BuyerCode }, and call the result **$ExistingBidRound****
2. **Decision:** "not exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Create Object
      - Store the result in a new variable called **$NewBidRound****
4. **Change List**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
