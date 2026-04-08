# Microflow Detailed Specification: SUB_Buyer_UploadExcelFileContent

### 📥 Inputs (Parameters)
- **$ManageFiletDocument** (Type: EcoATM_PWS.ManageFileDocument)
- **$BuyerOffer** (Type: EcoATM_PWS.BuyerOffer)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Update **$ManageFiletDocument**
      - Set **Message** = `'Extract file data content'`
      - Set **ProcessPercentage** = `10`**
3. **Call Microflow **EcoATM_PWS.SUB_BuyerOffer_RemoveRecords****
4. **ImportExcelData**
5. **Update **$ManageFiletDocument**
      - Set **Message** = `'Importing data in progress...'`
      - Set **ProcessPercentage** = `25`**
6. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/SKU!=empty` (Result: **$FilteredNonEmptyRows**)**
7. **Call Microflow **EcoATM_PWS.SUB_OfferBuyer_IsExcelDataSuccess** (Result: **$IsSuccessfullyImported**)**
8. **Update **$ManageFiletDocument**
      - Set **HasProcessFailed** = `$IsSuccessfullyImported`**
9. **Call Microflow **Custom_Logging.SUB_Log_Info****
10. 🏁 **END:** Return `$IsSuccessfullyImported`

**Final Result:** This process concludes by returning a [Boolean] value.