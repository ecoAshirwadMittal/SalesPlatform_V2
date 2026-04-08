# Microflow Analysis: ACT_ChangeSavedBidsToPreviouslySubmitted

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
2. **Retrieve
      - Store the result in a new variable called **$BidRoundList****
3. **Create List
      - Store the result in a new variable called **$BidDataList_ToCommit****
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Permanently save **$undefined** to the database.**
6. **Run another process: "Custom_Logging.SUB_Log_EndTimer"
      - Store the result in a new variable called **$Log****
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
