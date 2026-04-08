# Microflow Analysis: SUB_BidDataCustomExcelExport_PopulateDocWithoutDownload

### Requirements (Inputs):
- **$Template** (A record of type: XLSReport.MxTemplate)
- **$BidDataDoc** (A record of type: AuctionUI.BidDataDoc)
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$BidRound** (A record of type: AuctionUI.BidRound)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
2. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
3. **Decision:** "BidData Doc Name?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Create Variable**
5. **Create Variable**
6. **Create Variable**
7. **Run another process: "Custom_Logging.SUB_Log_Info"**
8. **Java Action Call
      - Store the result in a new variable called **$Document**** ⚠️ *(This step has a safety catch if it fails)*
9. **Permanently save **$undefined** to the database.**
10. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
