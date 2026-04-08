# Microflow Analysis: SUB_NavigateToCounterOffers

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.Offer** using filter: { [EcoATM_PWS.Offer_BuyerCode=$BuyerCode]
[OfferStatus='Buyer_Acceptance']
 } (Call this list **$OfferList**)**
2. **Aggregate List
      - Store the result in a new variable called **$Count****
3. **Decision:** "Count=0?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Show Page**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
