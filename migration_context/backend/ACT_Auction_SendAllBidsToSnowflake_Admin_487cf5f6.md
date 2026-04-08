# Microflow Analysis: ACT_Auction_SendAllBidsToSnowflake_Admin

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
2. **Decision:** "send to snowflake?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Search the Database for **AuctionUI.BidRound** using filter: { [AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction=$Auction] } (Call this list **$BidRoundListSorted_All**)**
4. **Take the list **$BidRoundListSorted_All**, perform a [Filter] where: { true }, and call the result **$BidRoundList_submitted****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
