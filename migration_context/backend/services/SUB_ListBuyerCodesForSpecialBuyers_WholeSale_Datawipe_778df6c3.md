# Microflow Analysis: SUB_ListBuyerCodesForSpecialBuyers_WholeSale_Datawipe

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Search the Database for **EcoATM_BuyerManagement.Buyer** using filter: { [isSpecialBuyer=true]
 } (Call this list **$BuyerList**)**
2. **Create List
      - Store the result in a new variable called **$SpecialBuyerCodeList****
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
