# Microflow Analysis: SUB_CheckForCounterOfferItems

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
2. **Take the list **$OfferItemList**, perform a [Filter] where: { EcoATM_PWS.ENUM_OfferItemStatus.Counter }, and call the result **$CounterOfferList****
3. **Decision:** "counter items exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.CounterItemsExist] to: "true"
      - Change [EcoATM_PWS.Offer.UpdateDate] to: "[%CurrentDateTime%]"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
