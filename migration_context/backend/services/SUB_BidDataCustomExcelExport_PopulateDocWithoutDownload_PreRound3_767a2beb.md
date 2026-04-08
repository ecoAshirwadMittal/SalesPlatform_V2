# Microflow Analysis: SUB_BidDataCustomExcelExport_PopulateDocWithoutDownload_PreRound3

### Requirements (Inputs):
- **$Template** (A record of type: XLSReport.MxTemplate)
- **$BidDataDoc** (A record of type: AuctionUI.BidDataDoc)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Decision:** "BidData Doc Name?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Create Variable**
4. **Create Variable**
5. **Create Variable**
6. **Run another process: "Custom_Logging.SUB_Log_Info"**
7. **Java Action Call
      - Store the result in a new variable called **$Document**** ⚠️ *(This step has a safety catch if it fails)*
8. **Permanently save **$undefined** to the database.**
9. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
