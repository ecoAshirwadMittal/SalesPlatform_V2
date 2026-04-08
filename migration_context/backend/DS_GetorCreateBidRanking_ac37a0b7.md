# Microflow Analysis: DS_GetorCreateBidRanking

### Execution Steps:
1. **Search the Database for **AuctionUI.BidRanking** using filter: { Show everything } (Call this list **$BidRanking**)**
2. **Decision:** "exists?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
