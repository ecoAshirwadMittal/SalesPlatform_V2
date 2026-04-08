# Microflow Analysis: ACT_BidData_ExcelImport

### Requirements (Inputs):
- **$BidUploadPageHelper** (A record of type: AuctionUI.BidUploadPageHelper)
- **$BidRound** (A record of type: AuctionUI.BidRound)
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$ImportFile** (A record of type: Custom_Excel_Import.ImportFile)
- **$Parent_NPBuyerCodeSelectHelper** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction****
2. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
3. **Update the **$undefined** (Object):
      - Change [Custom_Excel_Import.ImportFile.ErrorMessage] to: "empty"**
4. **Decision:** "Import File empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Log Message**
6. **Run another process: "EcoATM_BidData.SUB_GetBidImportSheetName"
      - Store the result in a new variable called **$SheetName****
7. **Decision:** "sheet name = 'BidDataExport'"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **sheet name = 'BidDataRankRound2Export'**
8. **Run another process: "EcoATM_BidData.SUB_BidDataImport_NoRank"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
