# Microflow Analysis: SUB_BidDataCustomExcelExport

### Requirements (Inputs):
- **$Template** (A record of type: XLSReport.MxTemplate)
- **$BidDataDoc** (A record of type: AuctionUI.BidDataDoc)
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Decision:** "BidData Doc Name?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Decision:** "BidRouterHelper?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Create Variable**
5. **Create Variable**
6. **Create Variable**
7. **Run another process: "Custom_Logging.SUB_Log_Info"**
8. **Java Action Call
      - Store the result in a new variable called **$Document**** ⚠️ *(This step has a safety catch if it fails)*
9. **Permanently save **$undefined** to the database.**
10. **Download File** ⚠️ *(This step has a safety catch if it fails)*
11. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
