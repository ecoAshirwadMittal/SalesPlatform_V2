# Microflow Analysis: ACT_OpenPWSOfferItems

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Decision:** "sales review?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Buyer Acceptance?**
2. **Java Action Call
      - Store the result in a new variable called **$ObjectInfo****
3. **Decision:** "isLocked?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
5. **Take the list **$OfferItemList**, perform a [FindByExpression] where: { $currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade != 'A_YYY'
and 
$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS' }, and call the result **$BuyerOfferItem_FunctionalDevice****
6. **Take the list **$OfferItemList**, perform a [FindByExpression] where: { $currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade = 'A_YYY'
and 
$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS' }, and call the result **$BuyerOfferItem_UntestedDevice****
7. **Take the list **$OfferItemList**, perform a [FindByExpression] where: { $currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'SPB' }, and call the result **$BuyerOfferItem_CaseLot****
8. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.IsFunctionalDeviceExist] to: "$BuyerOfferItem_FunctionalDevice != empty"
      - Change [EcoATM_PWS.Offer.IsCaseLotsExist] to: "$BuyerOfferItem_CaseLot != empty"
      - Change [EcoATM_PWS.Offer.IsUntestedDeviceExist] to: "$BuyerOfferItem_UntestedDevice != empty"
      - **Save:** This change will be saved to the database immediately.**
9. **Run another process: "Custom_Logging.SUB_Log_Info"**
10. **Run another process: "EcoATM_PWS.SUB_CheckifAllOfferItemsAccepted"**
11. **Show Page**
12. **Run another process: "Custom_Logging.SUB_Log_Info"**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
