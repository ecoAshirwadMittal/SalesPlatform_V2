# Microflow Analysis: SUB_GenerateReservedQuantityForDevice

### Requirements (Inputs):
- **$Device** (A record of type: EcoATM_PWSMDM.Device)

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [EcoATM_PWS.OfferItem_Device=$Device]
[SalesOfferItemStatus= 'Accept' or SalesOfferItemStatus= 'Finalize' or (SalesOfferItemStatus='Counter' and BuyerCounterStatus='Accept')]
[(EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer
[OfferStatus= 'Sales_Review' or  OfferStatus='Buyer_Acceptance' or
OfferStatus= 'Pending_Order' ])or (EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus='Ordered' and not (OrderSynced))
]
 } (Call this list **$OfferItemList**)**
2. **Aggregate List
      - Store the result in a new variable called **$TotalReservedQuantity****
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Decimal] result.
