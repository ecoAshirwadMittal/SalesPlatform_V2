# Microflow Analysis: ACT_DeleteBidDataByBuyer

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Retrieve
      - Store the result in a new variable called **$BidDataList****
3. **Delete**
4. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
5. **Delete**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
