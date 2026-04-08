# Microflow Analysis: VAL_Offer_IsCounterOfferReadyForSubmit

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
2. **Take the list **$OfferItemList**, perform a [Filter] where: { EcoATM_PWS.ENUM_OfferItemStatus.Counter }, and call the result **$CounterOfferItemList****
3. **Decision:** "exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Take the list **$CounterOfferItemList**, perform a [Filter] where: { empty }, and call the result **$MissingBuyerCounterStatusOfferItemList****
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
