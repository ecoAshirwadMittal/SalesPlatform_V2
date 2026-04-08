# Microflow Analysis: SUB_CalculateOfferSummary_NoCounterItems

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
3. **Take the list **$OfferItemList**, perform a [Filter] where: { EcoATM_PWS.ENUM_OfferItemStatus.Accept }, and call the result **$OfferItemList_Accept****
4. **Take the list **$OfferItemList_Accept**, perform a [FilterByExpression] where: { $currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType='PWS' }, and call the result **$OfferItemList_PWS****
5. **Aggregate List
      - Store the result in a new variable called **$SKUs_PWS****
6. **Run another process: "EcoATM_PWS.SUB_OfferItem_CalculateCaseLotSKUs"
      - Store the result in a new variable called **$SKUs_CaseLots****
7. **Aggregate List
      - Store the result in a new variable called **$SumFinalOfferQuantity****
8. **Aggregate List
      - Store the result in a new variable called **$SumFinalOfferTotalPrice****
9. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.CounterOfferTotalSKU] to: "empty
"
      - Change [EcoATM_PWS.Offer.CounterOfferTotalQty] to: "empty
"
      - Change [EcoATM_PWS.Offer.CounterOfferTotalPrice] to: "empty"
      - Change [EcoATM_PWS.Offer.CounterOfferAvgPrice] to: "empty"
      - Change [EcoATM_PWS.Offer.CounterOfferMinPercentageVariance] to: "empty"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalSKU] to: "$SKUs_PWS+$SKUs_CaseLots"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalQty] to: "$SumFinalOfferQuantity"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalPrice] to: "$SumFinalOfferTotalPrice"
      - **Save:** This change will be saved to the database immediately.**
10. **Run another process: "Custom_Logging.SUB_Log_Info"**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
