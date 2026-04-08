# Microflow Analysis: SUB_CalculateFinalizeOfferSummary

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Retrieve
      - Store the result in a new variable called **$FinalizeOfferItemList****
3. **Take the list **$FinalizeOfferItemList**, perform a [Filter] where: { EcoATM_PWS.ENUM_OfferItemStatus.Finalize }, and call the result **$OnlyFinalizeOfferItemList****
4. **Aggregate List
      - Store the result in a new variable called **$SKUs_Finalize****
5. **Aggregate List
      - Store the result in a new variable called **$SumFinalOfferQuantity****
6. **Aggregate List
      - Store the result in a new variable called **$Sum_FinalOfferTotalPrice****
7. **Aggregate List
      - Store the result in a new variable called **$Sum_FinalOfferPrice****
8. **Create Variable**
9. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
10. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.CounterOfferTotalSKU] to: "$SKUs_Finalize
"
      - Change [EcoATM_PWS.Offer.CounterOfferTotalQty] to: "$SumFinalOfferQuantity
"
      - Change [EcoATM_PWS.Offer.CounterOfferTotalPrice] to: "$Sum_FinalOfferTotalPrice"
      - Change [EcoATM_PWS.Offer.CounterOfferAvgPrice] to: "round($Sum_FinalOfferTotalPrice div $SumFinalOfferQuantity)"
      - Change [EcoATM_PWS.Offer.CounterOfferMinPercentageVariance] to: "(($SumDifference)div $Sum_FinalOfferPrice)*100"
      - Change [EcoATM_PWS.Offer.UpdateDate] to: "[%CurrentDateTime%]"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalSKU] to: "$SKUs_Finalize"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalQty] to: "$SumFinalOfferQuantity"
      - Change [EcoATM_PWS.Offer.FinalOfferTotalPrice] to: "$Sum_FinalOfferTotalPrice"
      - **Save:** This change will be saved to the database immediately.**
11. **Run another process: "Custom_Logging.SUB_Log_Info"**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
