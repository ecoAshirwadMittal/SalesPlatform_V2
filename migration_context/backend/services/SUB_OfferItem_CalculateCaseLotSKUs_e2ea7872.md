# Microflow Analysis: SUB_OfferItem_CalculateCaseLotSKUs

### Requirements (Inputs):
- **$OfferItemList** (A record of type: EcoATM_PWS.OfferItem)

### Execution Steps:
1. **Take the list **$OfferItemList**, perform a [FilterByExpression] where: { $currentObject/EcoATM_PWS.OfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/EcoATM_PWSMDM.CaseLot_Device/EcoATM_PWSMDM.Device/ItemType='SPB' }, and call the result **$OfferItemList_SPB****
2. **Create List
      - Store the result in a new variable called **$UniqueSKUs****
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Aggregate List
      - Store the result in a new variable called **$Count****
5. **Delete**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Integer] result.
