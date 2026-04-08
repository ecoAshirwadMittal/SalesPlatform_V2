# Microflow Analysis: SUB_CheckifAllOfferItemsAccepted

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
2. **Take the list **$OfferItemList**, perform a [FilterByExpression] where: { $currentObject/SalesOfferItemStatus = EcoATM_PWS.ENUM_OfferItemStatus.Finalize
or
$currentObject/SalesOfferItemStatus = EcoATM_PWS.ENUM_OfferItemStatus.Counter }, and call the result **$OfferItemList_CounterOrFinalize****
3. **Decision:** "all are valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Run another process: "EcoATM_PWS.SUB_CalculateCounterOfferSummary"**
5. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.isValidOffer] to: "true"
      - **Save:** This change will be saved to the database immediately.**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
