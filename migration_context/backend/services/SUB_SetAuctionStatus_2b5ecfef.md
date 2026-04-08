# Microflow Analysis: SUB_SetAuctionStatus

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList_2****
2. **Take the list **$SchedulingAuctionList_2**, perform a [FilterByExpression] where: { $currentObject/RoundStatus = AuctionUI.enum_SchedulingAuctionStatus.Started 
or 
$currentObject/RoundStatus  = AuctionUI.enum_SchedulingAuctionStatus.Closed
or 
$currentObject/RoundStatus  = AuctionUI.enum_SchedulingAuctionStatus.Scheduled }, and call the result **$SchedulingAuctionList****
3. **Take the list **$SchedulingAuctionList**, perform a [FilterByExpression] where: { $currentObject/RoundStatus!=AuctionUI.enum_SchedulingAuctionStatus.Closed }, and call the result **$NewSchedulingAuctionList_notClosed****
4. **Decision:** "all rounds closed?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Update the **$undefined** (Object):
      - Change [AuctionUI.Auction.AuctionStatus] to: "AuctionUI.enum_SchedulingAuctionStatus.Closed
"**
6. **Permanently save **$undefined** to the database.**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
