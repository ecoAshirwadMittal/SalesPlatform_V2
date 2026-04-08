# Microflow Analysis: ACT_ListHighBids

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Retrieve
      - Store the result in a new variable called **$Auction****
3. **Retrieve
      - Store the result in a new variable called **$Week****
4. **Create Variable**
5. **Java Action Call
      - Store the result in a new variable called **$MaxLotBidList**** ⚠️ *(This step has a safety catch if it fails)*
6. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
