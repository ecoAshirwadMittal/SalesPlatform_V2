# Microflow Analysis: SUB_GetBidRoundBySAandCode

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Search the Database for **AuctionUI.BidRound** using filter: { [
  (
    AuctionUI.BidRound_BuyerCode = $BuyerCode
    and AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction
  )
] } (Call this list **$BidRound**)**
2. **Decision:** "not exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Create Object
      - Store the result in a new variable called **$NewBidRound****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
