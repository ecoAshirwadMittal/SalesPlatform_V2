# Microflow Analysis: SUB_Buyer_UploadExcelFileContent

### Requirements (Inputs):
- **$ManageFiletDocument** (A record of type: EcoATM_PWS.ManageFileDocument)
- **$BuyerOffer** (A record of type: EcoATM_PWS.BuyerOffer)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.ManageFileDocument.Message] to: "'Extract file data content'
"
      - Change [EcoATM_PWS.ManageFileDocument.ProcessPercentage] to: "10
"**
3. **Run another process: "EcoATM_PWS.SUB_BuyerOffer_RemoveRecords"**
4. **Import Excel Data
      - Store the result in a new variable called **$OfferDataExcelImporterList**** ⚠️ *(This step has a safety catch if it fails)*
5. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.ManageFileDocument.Message] to: "'Importing data in progress...'
"
      - Change [EcoATM_PWS.ManageFileDocument.ProcessPercentage] to: "25
"**
6. **Take the list **$OfferDataExcelImporterList**, perform a [FilterByExpression] where: { $currentObject/SKU!=empty }, and call the result **$FilteredNonEmptyRows****
7. **Run another process: "EcoATM_PWS.SUB_OfferBuyer_IsExcelDataSuccess"
      - Store the result in a new variable called **$IsSuccessfullyImported****
8. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.ManageFileDocument.HasProcessFailed] to: "$IsSuccessfullyImported
"**
9. **Run another process: "Custom_Logging.SUB_Log_Info"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
