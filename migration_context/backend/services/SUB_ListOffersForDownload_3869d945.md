# Microflow Analysis: SUB_ListOffersForDownload

### Requirements (Inputs):
- **$OfferMasterHelper** (A record of type: EcoATM_PWS.OfferMasterHelper)

### Execution Steps:
1. **Decision:** "Ordered?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Total?**
2. **Search the Database for **EcoATM_PWS.Offer** using filter: { [OfferStatus='Ordered' or OfferStatus='Pending_Order'] } (Call this list **$OfferList_OrderedPending**)**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
