# Microflow Analysis: SUB_CheckIfQtyAdjusted

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
2. **Take the list **$OfferItemList**, perform a [FindByExpression] where: { $currentObject/FinalOfferQuantity<$currentObject/OfferQuantity }, and call the result **$OfferItemWithAdjustedQty****
3. **Decision:** "found?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
