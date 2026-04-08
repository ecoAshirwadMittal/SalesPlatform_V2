# Microflow Analysis: SUB_Round3_BidData_TransformAndCommit_UsingPreProcessedData

### Requirements (Inputs):
- **$ExcelIMport_BidData** (A record of type: AuctionUI.BidDataImport_Round3)
- **$RoundThreeBidDataExcelExport** (A record of type: AuctionUI.RoundThreeBidDataExcelExport)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$BidRoundList** (A record of type: AuctionUI.BidRound)
- **$Buyer** (A record of type: EcoATM_BuyerManagement.Buyer)
- **$RoundThreeBuyersDataReport** (A record of type: AuctionUI.RoundThreeBuyersDataReport)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Create Variable**
3. **Retrieve
      - Store the result in a new variable called **$Auction****
4. **Retrieve
      - Store the result in a new variable called **$Week****
5. **Run another process: "AuctionUI.ACT_DeleteRound3BidDataForBuyer"**
6. **Retrieve
      - Store the result in a new variable called **$BuyerCodeList_BuyerAllCodes****
7. **Create List
      - Store the result in a new variable called **$BidDataList_Updates****
8. **Retrieve
      - Store the result in a new variable called **$AggregatedInventoryList****
9. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
10. **Decision:** "Sucess?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
11. **Take the list **$BidRoundList**, perform a [Union], and call the result **$BidRoundList_Distinct****
12. **Change List**
13. **Delete**
14. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
15. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
