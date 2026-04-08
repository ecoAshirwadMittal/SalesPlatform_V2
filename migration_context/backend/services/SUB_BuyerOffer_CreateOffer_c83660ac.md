# Microflow Analysis: SUB_BuyerOffer_CreateOffer

### Requirements (Inputs):
- **$BuyerOffer** (A record of type: EcoATM_PWS.BuyerOffer)
- **$BuyerOfferItemList** (A record of type: EcoATM_PWS.BuyerOfferItem)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$ENUM_OfferType** (A record of type: Object)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Search the Database for **Administration.Account** using filter: { [id = $currentUser] } (Call this list **$Account**)**
5. **Take the list **$BuyerOfferItemList**, perform a [FindByExpression] where: { $currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade != 'A_YYY'
and 
$currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS' }, and call the result **$BuyerOfferItem_FunctionalDevice****
6. **Take the list **$BuyerOfferItemList**, perform a [FindByExpression] where: { $currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade = 'A_YYY'
and 
$currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS' }, and call the result **$BuyerOfferItem_UntestedDevice****
7. **Take the list **$BuyerOfferItemList**, perform a [FindByExpression] where: { $currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'SPB' }, and call the result **$BuyerOfferItem_CaseLot****
8. **Create Object
      - Store the result in a new variable called **$NewOffer****
9. **Run another process: "EcoATM_PWS.ACr_UpdateOfferID"**
10. **Create List
      - Store the result in a new variable called **$OfferItemList****
11. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
12. **Aggregate List
      - Store the result in a new variable called **$SumTotalQuantity****
13. **Aggregate List
      - Store the result in a new variable called **$SumTotalPrice****
14. **Run another process: "EcoATM_PWS.SUB_RetrieveOrderStatus"
      - Store the result in a new variable called **$OrderStatus****
15. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.OfferTotalQuantity] to: "$SumTotalQuantity
"
      - Change [EcoATM_PWS.Offer.OfferTotalPrice] to: "$SumTotalPrice
"
      - Change [EcoATM_PWS.Offer.OfferStatus] to: "EcoATM_PWS.ENUM_PWSOrderStatus.Sales_Review
"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalSKU] to: "$BuyerOffer/OfferSKUs"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalQty] to: "$SumTotalQuantity"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalPrice] to: "$SumTotalPrice"
      - Change [EcoATM_PWS.Offer.UpdateDate] to: "[%CurrentDateTime%]
"
      - Change [EcoATM_PWS.Offer_OrderStatus] to: "$OrderStatus
"**
16. **Run another process: "EcoATM_PWS.SUB_CreateOriginalOfferSummary"
      - Store the result in a new variable called **$OriginalOfferSummary****
17. **Permanently save **$undefined** to the database.**
18. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
19. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
