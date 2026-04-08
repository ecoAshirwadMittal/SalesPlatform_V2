# Microflow Analysis: SUB_CheckAllDeclined

### Requirements (Inputs):
- **$OfferItemList** (A record of type: EcoATM_PWS.OfferItem)

### Execution Steps:
1. **Aggregate List
      - Store the result in a new variable called **$OfferItemCount****
2. **Take the list **$OfferItemList**, perform a [Filter] where: { EcoATM_PWS.ENUM_OfferItemStatus.Decline }, and call the result **$DeclineItemList****
3. **Aggregate List
      - Store the result in a new variable called **$DeclineCount****
4. **Decision:** "all items declined?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
