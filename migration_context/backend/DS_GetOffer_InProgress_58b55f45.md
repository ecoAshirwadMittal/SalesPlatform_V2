# Microflow Analysis: DS_GetOffer_InProgress

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Create Variable**
5. **Search the Database for **EcoATM_PWS.BuyerOffer** using filter: { [EcoATM_PWS.BuyerOffer_BuyerCode = $BuyerCode]
[OfferStatus = $InProgress_OrderStatus]
 } (Call this list **$LastBuyerOffer**)**
6. **Decision:** "Order Available?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Retrieve
      - Store the result in a new variable called **$BuyerOfferItemList****
8. **Run another process: "EcoATM_PWS.SUB_CalculateTotals_OnChange"**
9. **Take the list **$BuyerOfferItemList**, perform a [FilterByExpression] where: { $currentObject/TotalPrice != empty
and
$currentObject/TotalPrice > 0 }, and call the result **$BuyerOfferItemList_Valid****
10. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
11. **Take the list **$BuyerOfferItemList**, perform a [FilterByExpression] where: { $currentObject/Quantity=empty
and
$currentObject/TotalPrice=0 }, and call the result **$BuyerOfferItemList_TotalZero****
12. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
13. **Take the list **$BuyerOfferItemList**, perform a [FilterByExpression] where: { ($currentObject/Quantity = empty or $currentObject/Quantity > 0) and 
(
if $currentObject/_Type = EcoATM_PWS.ENUM_BuyerOfferItemType.Functional_Case_Lot
then
$currentObject/CaseOfferTotalPrice != empty
else
$currentObject/TotalPrice != empty 
) }, and call the result **$BuyerOfferItemList_Modified****
14. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
15. **Permanently save **$undefined** to the database.**
16. **Take the list **$BuyerOfferItemList_Valid**, perform a [Find] where: { true }, and call the result **$BuyerOfferItem_ExceedingQty****
17. **Take the list **$BuyerOfferItemList_Valid**, perform a [FindByExpression] where: { $currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'SPB'
and
$currentObject/IsModified }, and call the result **$BuyerOfferItem_ModifiedCaseLot****
18. **Take the list **$BuyerOfferItemList_Valid**, perform a [FindByExpression] where: { $currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS'
and
$currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade != 'A_YYY'
and
$currentObject/IsModified }, and call the result **$BuyerOfferItem_ModifiedFunctionalDevice****
19. **Take the list **$BuyerOfferItemList_Valid**, perform a [FindByExpression] where: { $currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS'
and
$currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade = 'A_YYY'
and
$currentObject/IsModified }, and call the result **$BuyerOfferItem_ModifiedUntestedDevice****
20. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.BuyerOffer.IsExceedingQty] to: "$BuyerOfferItem_ExceedingQty != empty"
      - Change [EcoATM_PWS.BuyerOffer.IsFunctionalDevicesExist] to: "$BuyerOfferItem_ModifiedFunctionalDevice != empty"
      - Change [EcoATM_PWS.BuyerOffer.IsCaseLotsExist] to: "$BuyerOfferItem_ModifiedCaseLot != empty"
      - Change [EcoATM_PWS.BuyerOffer.IsUntestedDeviceExist] to: "$BuyerOfferItem_ModifiedUntestedDevice != empty"
      - **Save:** This change will be saved to the database immediately.**
21. **Permanently save **$undefined** to the database.**
22. **Permanently save **$undefined** to the database.**
23. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
24. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
