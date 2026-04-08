# Microflow Analysis: SUB_OfferBuyer_IsExcelDataSuccess_3

### Requirements (Inputs):
- **$OfferDataExcelImporterList** (A record of type: EcoATM_PWS.OfferDataExcelImporter)
- **$BuyerCode** (A record of type: AuctionUI.BuyerCode)
- **$ManageFileDocument** (A record of type: EcoATM_PWS.ManageFileDocument)

### Execution Steps:
1. **Decision:** "data?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Take the list **$OfferDataExcelImporterList**, perform a [FilterByExpression] where: { trim($currentObject/Quantity)='' or trim($currentObject/Quantity)='0' }, and call the result **$EmptyOrZeoQuantityList****
3. **Decision:** "Quantity defined?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Create List
      - Store the result in a new variable called **$UnresolvedSKUOfferDataExcelImporterList****
5. **Create List
      - Store the result in a new variable called **$DuplicateOfferDataExcelImporterList****
6. **Create List
      - Store the result in a new variable called **$CorrespondingDeviceList****
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Decision:** "is data compliant?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
9. **Create List
      - Store the result in a new variable called **$BuyerOfferList****
10. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
11. **Permanently save **$undefined** to the database.**
12. **Delete**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
