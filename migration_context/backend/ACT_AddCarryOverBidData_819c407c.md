# Microflow Analysis: ACT_AddCarryOverBidData

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)
- **$LastWeek_BidDataList** (A record of type: AuctionUI.BidData)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Retrieve
      - Store the result in a new variable called **$ScheduleAuction****
3. **Retrieve
      - Store the result in a new variable called **$Auction****
4. **Retrieve
      - Store the result in a new variable called **$Week****
5. **Create List
      - Store the result in a new variable called **$BidDataList_Updates****
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Permanently save **$undefined** to the database.**
8. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
