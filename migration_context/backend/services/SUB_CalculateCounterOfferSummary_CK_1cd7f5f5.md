# Microflow Analysis: SUB_CalculateCounterOfferSummary_CK

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
3. **Aggregate List
      - Store the result in a new variable called **$SKUs_Counter****
4. **Create Variable**
5. **Create Variable**
6. **Create Variable**
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.CounterOfferTotalSKU] to: "$SKUs_Counter
"
      - Change [EcoATM_PWS.Offer.CounterOfferTotalQty] to: "parseInteger(toString($CounterQty))
"
      - Change [EcoATM_PWS.Offer.CounterOfferTotalPrice] to: "parseInteger(toString($CounterPriceTotal))"
      - Change [EcoATM_PWS.Offer.CounterOfferAvgPrice] to: "round($CounterPriceTotal div $CounterQty)"
      - Change [EcoATM_PWS.Offer.CounterOfferMinPercentageVariance] to: "(($SumDifference)div $CounterPriceTotal)*100"
      - Change [EcoATM_PWS.Offer.UpdateDate] to: "[%CurrentDateTime%]"
      - **Save:** This change will be saved to the database immediately.**
9. **Run another process: "Custom_Logging.SUB_Log_Info"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
