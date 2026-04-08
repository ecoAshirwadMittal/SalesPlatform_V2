# Microflow Analysis: VAL_Offer_Finalize

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)
- **$OfferItemList** (A record of type: EcoATM_PWS.OfferItem)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Take the list **$OfferItemList**, perform a [Filter] where: { EcoATM_PWS.ENUM_OfferItemStatus.Finalize }, and call the result **$FinalizeOfferItemList****
3. **Decision:** "Finalize exit?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Take the list **$OfferItemList**, perform a [FilterByExpression] where: { $currentObject/SalesOfferItemStatus = EcoATM_PWS.ENUM_OfferItemStatus.Counter
or
$currentObject/SalesOfferItemStatus = EcoATM_PWS.ENUM_OfferItemStatus.Accept
or 
$currentObject/SalesOfferItemStatus=empty }, and call the result **$EmptyOrCounterOrAccepteOfferItemList****
5. **Decision:** "Counter or Accept does not exists?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
