# Microflow Analysis: SUB_Buyer_UploadExcelFile_2

### Requirements (Inputs):
- **$OfferExcelImportDocument** (A record of type: EcoATM_PWS.ManageFileDocument)

### Execution Steps:
1. **Run another process: "EcoATM_PWS.VAL_OfferBuyerExcelFile_IsValid"
      - Store the result in a new variable called **$IsValid****
2. **Decision:** "IsValid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Import Excel Data
      - Store the result in a new variable called **$OfferDataExcelImporterList**** ⚠️ *(This step has a safety catch if it fails)*
4. **Retrieve
      - Store the result in a new variable called **$Offer****
5. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
6. **Run another process: "EcoATM_PWS.SUB_OfferBuyer_IsExcelDataSuccess"
      - Store the result in a new variable called **$IsSuccessfullyImported****
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
