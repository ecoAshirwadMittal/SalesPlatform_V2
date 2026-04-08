# Microflow Analysis: ACT_Round3_BidData_Import_ClientController_PreProcess

### Requirements (Inputs):
- **$RoundThreeBidDataExcelExport** (A record of type: AuctionUI.RoundThreeBidDataExcelExport)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$Buyer** (A record of type: EcoATM_BuyerManagement.Buyer)
- **$RoundThreeBuyersDataReport** (A record of type: AuctionUI.RoundThreeBuyersDataReport)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Update the **$undefined** (Object):
      - Change [AuctionUI.RoundThreeBidDataExcelExport.ErrorMessage] to: "empty"**
3. **Decision:** "Import File empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Run another process: "AuctionUI.SUB_Round3_ExcelImport_PreProcess"
      - Store the result in a new variable called **$ExcelImport_BidDataRound3**** ⚠️ *(This step has a safety catch if it fails)*
5. **Decision:** "Rows imported?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Create List
      - Store the result in a new variable called **$BidRoundList****
7. **Run another process: "AuctionUI.SUB_Round3_BidData_TransformAndCommit_UsingPreProcessedData"
      - Store the result in a new variable called **$BidData_ToCOmmit**** ⚠️ *(This step has a safety catch if it fails)*
8. **Decision:** "Bid Data list not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
9. **Aggregate List
      - Store the result in a new variable called **$RowsUploaded****
10. **Close Form**
11. **Permanently save **$undefined** to the database.**
12. **Permanently save **$undefined** to the database.**
13. **Delete**
14. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
15. **Update the **$undefined** (Object):
      - Change [AuctionUI.RoundThreeBuyersDataReport.SubmittedBy] to: "$currentUser/Name"
      - Change [AuctionUI.RoundThreeBuyersDataReport.SubmittedOn] to: "[%CurrentDateTime%]"
      - **Save:** This change will be saved to the database immediately.**
16. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
17. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
