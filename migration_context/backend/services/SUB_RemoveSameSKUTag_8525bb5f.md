# Microflow Analysis: SUB_RemoveSameSKUTag

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.Offer** using filter: { [OfferStatus = 'Ordered' or OfferStatus = 'Declined']
[SameSKUOffer] } (Call this list **$OfferList_OrderedOrDeclinedWithSKU**)**
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [SameSKUOfferAvailable]
[EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus = 'Ordered' or EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus = 'Declined']  } (Call this list **$OfferItemList**)**
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Permanently save **$undefined** to the database.**
6. **Permanently save **$undefined** to the database.**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
