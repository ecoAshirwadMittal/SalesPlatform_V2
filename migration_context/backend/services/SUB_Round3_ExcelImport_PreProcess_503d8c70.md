# Microflow Analysis: SUB_Round3_ExcelImport_PreProcess

### Requirements (Inputs):
- **$RoundThreeBidDataExcelExport** (A record of type: AuctionUI.RoundThreeBidDataExcelExport)
- **$RoundThreeBuyersDataReport** (A record of type: AuctionUI.RoundThreeBuyersDataReport)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Import Excel Data
      - Store the result in a new variable called **$BidDataImport_Round3List****
3. **Take the list **$BidDataImport_Round3List**, perform a [FilterByExpression] where: { $currentObject/Accept_Max_Bid_YN != empty and toUpperCase($currentObject/Accept_Max_Bid_YN) != 'N' and trim($currentObject/Accept_Max_Bid_YN) != '' }, and call the result **$BidData_Round3List_Filtered****
4. **Retrieve
      - Store the result in a new variable called **$RoundThreeBidDataReportList****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
