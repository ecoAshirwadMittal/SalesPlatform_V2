# Microflow Analysis: SUB_CheckForFinalizeOfferItems

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
2. **Take the list **$OfferItemList**, perform a [Filter] where: { EcoATM_PWS.ENUM_OfferItemStatus.Finalize }, and call the result **$FinalizeOfferList****
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
