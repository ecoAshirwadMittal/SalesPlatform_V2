# Microflow Analysis: SUB_CopyCarryOverBidData

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)
- **$BidDataTemp_LastWeek** (A record of type: AuctionUI.BidDataTemp)
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Retrieve
      - Store the result in a new variable called **$ScheduleAuction****
5. **Retrieve
      - Store the result in a new variable called **$Auction****
6. **Retrieve
      - Store the result in a new variable called **$Week****
7. **Retrieve
      - Store the result in a new variable called **$AggregatedInventoryList****
8. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BidRound = $BidRound]
[AuctionUI.BidData_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code =$NP_BuyerCodeSelect_Helper/Code]
 } (Call this list **$BidData_CurrentWeek**)**
9. **Create List
      - Store the result in a new variable called **$BidDataList_Updates****
10. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
11. **Permanently save **$undefined** to the database.**
12. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
