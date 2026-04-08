# Microflow Analysis: ACT_BidDataDoc_GetOrCreate

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BidDataDoc****
2. **Decision:** "Bid Data Found?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
