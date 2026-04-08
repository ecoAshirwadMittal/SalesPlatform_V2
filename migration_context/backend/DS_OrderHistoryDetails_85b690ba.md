# Microflow Analysis: DS_OrderHistoryDetails

### Requirements (Inputs):
- **$OfferAndOrdersView** (A record of type: EcoATM_PWS.OfferAndOrdersView)

### Execution Steps:
1. **Decision:** "Is OrderNumber Avaiable?"
   - If [true] -> Move to: **Is Offer?**
   - If [false] -> Move to: **Finish**
2. **Decision:** "Is Offer?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [
  (
    EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferID = $OfferAndOrdersView/OrderNumber
  )
] } (Call this list **$OfferItemList**)**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
