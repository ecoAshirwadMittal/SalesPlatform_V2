# Microflow Analysis: SUB_ProcessLastWeekBidData

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Last_Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Create Variable**
5. **Java Action Call
      - Store the result in a new variable called **$BidList_LastWeek****
6. **Run another process: "EcoATM_BidData.SUB_CopyCarryOverBidData"
      - Store the result in a new variable called **$BidDataList****
7. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
