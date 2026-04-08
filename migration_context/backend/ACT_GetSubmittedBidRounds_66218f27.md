# Microflow Analysis: ACT_GetSubmittedBidRounds

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Run another process: "AuctionUI.ACT_GetMostRecentAuction"
      - Store the result in a new variable called **$Auction****
3. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList****
4. **Take the list **$SchedulingAuctionList**, perform a [Sort], and call the result **$SortedScheduledAuction****
5. **Create Object
      - Store the result in a new variable called **$ObjectBidderRouter****
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
