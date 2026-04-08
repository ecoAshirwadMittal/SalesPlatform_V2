# Microflow Analysis: SUB_BuyerOfferItem_CalculateCaseLotSKUs

### Requirements (Inputs):
- **$BuyerOfferItemList_SPB** (A record of type: EcoATM_PWS.BuyerOfferItem)

### Execution Steps:
1. **Create List
      - Store the result in a new variable called **$UniqueSKUs****
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Aggregate List
      - Store the result in a new variable called **$Count****
4. **Delete**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Integer] result.
