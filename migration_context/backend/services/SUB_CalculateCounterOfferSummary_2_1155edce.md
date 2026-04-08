# Microflow Analysis: SUB_CalculateCounterOfferSummary_2

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
3. **Take the list **$OfferItemList**, perform a [FilterByExpression] where: { $currentObject/SalesOfferItemStatus=empty or $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Decline }, and call the result **$ExcludedOfferItemList****
4. **Take the list **$OfferItemList**, perform a [Subtract], and call the result **$AcceptedOrCounteredOfferItemList****
5. **Aggregate List
      - Store the result in a new variable called **$SKUs_Counter****
6. **Aggregate List
      - Store the result in a new variable called **$SumFinalOfferQuantity****
7. **Aggregate List
      - Store the result in a new variable called **$Sum_FinalOfferTotalPrice****
8. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.CounterOfferTotalSKU] to: "$SKUs_Counter
"
      - Change [EcoATM_PWS.Offer.CounterOfferTotalQty] to: "$SumFinalOfferQuantity
"
      - Change [EcoATM_PWS.Offer.CounterOfferTotalPrice] to: "$Sum_FinalOfferTotalPrice"
      - Change [EcoATM_PWS.Offer.CounterOfferAvgPrice] to: "round($Sum_FinalOfferTotalPrice div $SumFinalOfferQuantity)"
      - Change [EcoATM_PWS.Offer.CounterOfferMinPercentageVariance] to: "(($SumDifference)div $Sum_FinalOfferPrice)*100"
      - Change [EcoATM_PWS.Offer.UpdateDate] to: "[%CurrentDateTime%]"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalSKU] to: "$SKUs_Counter"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalQty] to: "$SumFinalOfferQuantity"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalPrice] to: "$Sum_FinalOfferTotalPrice"
      - **Save:** This change will be saved to the database immediately.**
9. **Run another process: "Custom_Logging.SUB_Log_Info"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
