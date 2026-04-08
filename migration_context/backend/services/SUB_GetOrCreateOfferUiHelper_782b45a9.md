# Microflow Analysis: SUB_GetOrCreateOfferUiHelper

### Requirements (Inputs):
- **$ENUM_PWSOrderStatus** (A record of type: Object)
- **$OfferMasterHelper** (A record of type: EcoATM_PWS.OfferMasterHelper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$OffersUiHelperList****
2. **Decision:** "list not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Take the list **$OffersUiHelperList**, perform a [Find] where: { $ENUM_PWSOrderStatus }, and call the result **$OffersUiHelperFound****
4. **Decision:** "found?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
