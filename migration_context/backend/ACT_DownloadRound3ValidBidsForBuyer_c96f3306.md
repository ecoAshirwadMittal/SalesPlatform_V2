# Microflow Analysis: ACT_DownloadRound3ValidBidsForBuyer

### Requirements (Inputs):
- **$RoundThreeBuyersData** (A record of type: AuctionUI.RoundThreeBuyersDataReport)
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [AuctionUI.SchedulingAuction_Auction = $Auction and Round = 3] } (Call this list **$Round3SchedulingAuction**)**
3. **Search the Database for **EcoATM_BuyerManagement.Buyer** using filter: { [
  (
    CompanyName = $RoundThreeBuyersData/CompanyName
  )
] } (Call this list **$Buyer**)**
4. **Run another process: "AuctionUI.ACT_DeleteRound3BidDataForBuyer"**
5. **Run another process: "AuctionUI.DS_Round3ValidBidsForBuyer"
      - Store the result in a new variable called **$RoundThreeBidDataReportList****
6. **Decision:** "data exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Create Object
      - Store the result in a new variable called **$NewRoundThreeBidDataExcelExport****
8. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name='RoundThreeBiddataExport'] } (Call this list **$MxTemplate**)**
9. **Create Variable**
10. **Java Action Call
      - Store the result in a new variable called **$ExcelExport**** ⚠️ *(This step has a safety catch if it fails)*
11. **Update the **$undefined** (Object):
      - Change [System.FileDocument.Name] to: "$FileName"**
12. **Download File**
13. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
