# Microflow Analysis: ACT_BidData_Import_ClientController

### Requirements (Inputs):
- **$BidUploadPageHelper** (A record of type: EcoATM_BidData.BidUploadPageHelper)
- **$BidRound** (A record of type: AuctionUI.BidRound)
- **$XMLDocumentTemplate** (A record of type: ExcelImporter.XMLDocumentTemplate)
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$BuyerCodeSelect_Helper** (A record of type: AuctionUI.BuyerCodeSelect_Helper)

### Execution Steps:
1. **Log Message**
2. **Retrieve
      - Store the result in a new variable called **$BidData_HelperList****
3. **Delete**
4. **Update the **$undefined** (Object):
      - Change [EcoATM_BidData.BidUploadPageHelper.ProgressValue] to: "15"
      - Change [EcoATM_BidData.BidUploadPageHelper.Status] to: "EcoATM_BidData.enum_BidUploadStatus.FileLoaded"
      - Change [EcoATM_BidData.BidUploadPageHelper.FileName] to: "$XMLDocumentTemplate/Name"
      - **Save:** This change will be saved to the database immediately.**
5. **Run another process: "EcoATM_BidData.BidData_ImportExcel"
      - Store the result in a new variable called **$isValidImport****
6. **Decision:** "Valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Update the **$undefined** (Object):
      - Change [EcoATM_BidData.BidUploadPageHelper.ProgressValue] to: "70"
      - Change [EcoATM_BidData.BidUploadPageHelper.Status] to: "EcoATM_BidData.enum_BidUploadStatus.BidDataImported"
      - Change [EcoATM_BidData.BidUploadPageHelper.ProcessingMessage] to: "'Validating Import'"
      - **Save:** This change will be saved to the database immediately.**
8. **Run another process: "EcoATM_BidData.BidData_Validate"
      - Store the result in a new variable called **$isValid****
9. **Decision:** "valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
10. **Update the **$undefined** (Object):
      - Change [EcoATM_BidData.BidUploadPageHelper.ProgressValue] to: "85"
      - Change [EcoATM_BidData.BidUploadPageHelper.ProcessingMessage] to: "'Creating Bids'"
      - **Save:** This change will be saved to the database immediately.**
11. **Run another process: "EcoATM_BidData.BidData_TransformAndCommit"
      - Store the result in a new variable called **$BidDataTo_Commit****
12. **Update the **$undefined** (Object):
      - Change [EcoATM_BidData.BidUploadPageHelper.ProgressValue] to: "100"
      - **Save:** This change will be saved to the database immediately.**
13. **Close Form**
14. **Run another process: "AuctionUI.SUB_AuctionTimerHelper"
      - Store the result in a new variable called **$AuctionTimerHelper****
15. **Permanently save **$undefined** to the database.**
16. **Show Page**
17. **Show Page**
18. **Log Message**
19. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
