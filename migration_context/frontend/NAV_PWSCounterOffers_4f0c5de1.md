# Microflow Analysis: NAV_PWSCounterOffers

### Execution Steps:
1. **Run another process: "EcoATM_PWS.DS_BuyerCodeBySession"
      - Store the result in a new variable called **$BuyerCode****
2. **Search the Database for **EcoATM_PWS.Offer** using filter: { [EcoATM_PWS.Offer_BuyerCode=$BuyerCode]
[OfferStatus='Buyer_Acceptance']
 } (Call this list **$OfferList**)**
3. **Aggregate List
      - Store the result in a new variable called **$Count****
4. **Decision:** "count =1 "
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Take the list **$OfferList**, perform a [Head], and call the result **$UniqueOffer****
6. **Java Action Call
      - Store the result in a new variable called **$ObjectInfo****
7. **Decision:** "isLocked?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
8. **Show Page**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
