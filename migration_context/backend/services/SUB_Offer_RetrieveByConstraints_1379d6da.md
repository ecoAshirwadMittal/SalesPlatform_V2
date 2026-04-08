# Microflow Analysis: SUB_Offer_RetrieveByConstraints

### Requirements (Inputs):
- **$ChangeOfferStatusHelper** (A record of type: EcoATM_PWS.ChangeOfferStatusHelper)
- **$OrderStatus** (A record of type: EcoATM_PWS.OrderStatus)

### Execution Steps:
1. **Decision:** "All Period?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Search the Database for **EcoATM_PWS.Offer** using filter: { [OfferStatus=$ChangeOfferStatusHelper/FromOfferStatus]
[EcoATM_PWS.Offer_OrderStatus/EcoATM_PWS.OrderStatus!=$OrderStatus]
 } (Call this list **$AllPeriodOfferList**)**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
