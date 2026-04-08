# Microflow Analysis: SUB_CreateOriginalOfferSummary

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Custom_Logging.SUB_Log_Info"**
3. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
4. **Take the list **$OfferItemList**, perform a [FilterByExpression] where: { $currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType= 'PWS' }, and call the result **$OfferItemList_PWS****
5. **Aggregate List
      - Store the result in a new variable called **$TotalSKUs_PWS****
6. **Run another process: "EcoATM_PWS.SUB_OfferItem_CalculateCaseLotSKUs"
      - Store the result in a new variable called **$TotalSKUs_CaseLots****
7. **Aggregate List
      - Store the result in a new variable called **$Qty****
8. **Aggregate List
      - Store the result in a new variable called **$Sum_OfferTotalPrice****
9. **Aggregate List
      - Store the result in a new variable called **$Sum_OfferPrice****
10. **Aggregate List
      - Store the result in a new variable called **$SumDifference****
11. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.OfferSKUCount] to: "$TotalSKUs_PWS+$TotalSKUs_CaseLots"
      - Change [EcoATM_PWS.Offer.OfferTotalQuantity] to: "$Qty"
      - Change [EcoATM_PWS.Offer.OfferTotalPrice] to: "$Sum_OfferTotalPrice"
      - Change [EcoATM_PWS.Offer.OfferAvgPrice] to: "round($Sum_OfferTotalPrice div $Qty)"
      - Change [EcoATM_PWS.Offer.OfferMinPercentageVariance] to: "(($SumDifference)div $Sum_OfferPrice)*100"
      - **Save:** This change will be saved to the database immediately.**
12. **Run another process: "Custom_Logging.SUB_Log_Info"**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
