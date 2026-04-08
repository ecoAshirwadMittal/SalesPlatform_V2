# Microflow Analysis: SUB_GetBidRoundByCodeWeek

### Requirements (Inputs):
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
2. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction****
3. **Retrieve
      - Store the result in a new variable called **$BidRound_BuyerCode****
4. **Search the Database for **AuctionUI.BidRound** using filter: { [AuctionUI.BidRound_BuyerCode=$BuyerCode
and AuctionUI.BidRound_SchedulingAuction=$SchedulingAuction] } (Call this list **$BidRound**)**
5. **Decision:** "$BidRound!=empty"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
