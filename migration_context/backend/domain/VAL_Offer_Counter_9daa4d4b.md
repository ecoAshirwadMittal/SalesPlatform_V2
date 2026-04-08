# Microflow Analysis: VAL_Offer_Counter

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)
- **$OfferItemList** (A record of type: EcoATM_PWS.OfferItem)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Custom_Logging.SUB_Log_Info"**
3. **Create Variable**
4. **Take the list **$OfferItemList**, perform a [FilterByExpression] where: { $currentObject/SalesOfferItemStatus = EcoATM_PWS.ENUM_OfferItemStatus.Counter }, and call the result **$CounterOfferItemList****
5. **Decision:** "list not empty?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Run another process: "Custom_Logging.SUB_Log_Info"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
