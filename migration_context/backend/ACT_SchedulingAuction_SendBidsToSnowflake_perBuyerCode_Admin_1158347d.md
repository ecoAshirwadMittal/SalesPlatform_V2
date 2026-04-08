# Microflow Analysis: ACT_SchedulingAuction_SendBidsToSnowflake_perBuyerCode_Admin

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)

### Execution Steps:
1. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
2. **Decision:** "send to snowflake?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
4. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction****
5. **Retrieve
      - Store the result in a new variable called **$Auction****
6. **Search the Database for **AuctionUI.BidRound** using filter: { [AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction=$Auction]
[AuctionUI.BidRound_BuyerCode = $BuyerCode] } (Call this list **$BidRoundListSorted_All**)**
7. **Take the list **$BidRoundListSorted_All**, perform a [Filter] where: { true }, and call the result **$BidRoundList_submitted****
8. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
