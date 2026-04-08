# Microflow Analysis: SUB_GetCurrentWeek

### Execution Steps:
1. **Run another process: "AuctionUI.ACT_GetMostRecentAuction"
      - Store the result in a new variable called **$Auction****
2. **Retrieve
      - Store the result in a new variable called **$Week****
3. **Decision:** "Wekk not empty?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
