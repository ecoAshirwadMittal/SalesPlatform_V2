# Microflow Analysis: SUB_CalculateCounterOfferSummary

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
3. **Take the list **$OfferItemList**, perform a [FilterByExpression] where: { $currentObject/SalesOfferItemStatus=empty or $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Decline }, and call the result **$ExcludedOfferItemList****
4. **Take the list **$OfferItemList**, perform a [Subtract], and call the result **$FinalOfferItemList****
5. **Take the list **$FinalOfferItemList**, perform a [FilterByExpression] where: { $currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType='PWS' }, and call the result **$FinalOfferItemList_PWS****
6. **Aggregate List
      - Store the result in a new variable called **$SKUs_PWS****
7. **Run another process: "EcoATM_PWS.SUB_OfferItem_CalculateCaseLotSKUs"
      - Store the result in a new variable called **$SKUs_CaseLots****
8. **Aggregate List
      - Store the result in a new variable called **$SumFinalOfferQuantity****
9. **Aggregate List
      - Store the result in a new variable called **$Sum_FinalOfferTotalPrice****
10. **Aggregate List
      - Store the result in a new variable called **$Sum_FinalOfferPrice****
11. **Create Variable**
12. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
13. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.CounterOfferTotalSKU] to: "$SKUs_PWS+$SKUs_CaseLots
"
      - Change [EcoATM_PWS.Offer.CounterOfferTotalQty] to: "$SumFinalOfferQuantity
"
      - Change [EcoATM_PWS.Offer.CounterOfferTotalPrice] to: "$Sum_FinalOfferTotalPrice"
      - Change [EcoATM_PWS.Offer.CounterOfferAvgPrice] to: "if($Sum_FinalOfferTotalPrice>0 and $SumFinalOfferQuantity>0) then
round($Sum_FinalOfferTotalPrice div $SumFinalOfferQuantity)
else
0"
      - Change [EcoATM_PWS.Offer.CounterOfferMinPercentageVariance] to: "if($Sum_FinalOfferPrice>0) then
(($SumDifference)div $Sum_FinalOfferPrice)*100
else
0"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalSKU] to: "$SKUs_PWS+$SKUs_CaseLots
"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalQty] to: "$SumFinalOfferQuantity"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalPrice] to: "$Sum_FinalOfferTotalPrice"
      - **Save:** This change will be saved to the database immediately.** ⚠️ *(This step has a safety catch if it fails)*
14. **Run another process: "Custom_Logging.SUB_Log_Info"**
15. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
