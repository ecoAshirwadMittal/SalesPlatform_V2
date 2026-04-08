# Microflow Analysis: SUB_OfferBuyer_IsExcelDataSuccess

### Requirements (Inputs):
- **$OfferDataExcelImporterList** (A record of type: EcoATM_PWS.OfferDataExcelImporter)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$ManageFileDocument** (A record of type: EcoATM_PWS.ManageFileDocument)
- **$BuyerOffer** (A record of type: EcoATM_PWS.BuyerOffer)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Decision:** "data?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Take the list **$OfferDataExcelImporterList**, perform a [FilterByExpression] where: { $currentObject/Quantity=empty or $currentObject/Quantity=0 }, and call the result **$EmptyOrZeoQuantityList****
4. **Decision:** "Quantity defined?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Create List
      - Store the result in a new variable called **$UnresolvedSKUOfferDataExcelImporterList****
6. **Create List
      - Store the result in a new variable called **$DuplicateOfferDataExcelImporterList****
7. **Create List
      - Store the result in a new variable called **$CorrespondingDeviceList****
8. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
9. **Decision:** "is data compliant?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
10. **Create Variable**
11. **Create List
      - Store the result in a new variable called **$BuyerOfferItemList****
12. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
13. **Run another process: "EcoATM_PWS.SUB_CalculateTotals"**
14. **Permanently save **$undefined** to the database.**
15. **Delete**
16. **Run another process: "Custom_Logging.SUB_Log_Info"**
17. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
