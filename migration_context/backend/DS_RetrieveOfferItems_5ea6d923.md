# Microflow Analysis: DS_RetrieveOfferItems

### Requirements (Inputs):
- **$Device** (A record of type: EcoATM_PWSMDM.Device)

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device=$Device] } (Call this list **$OfferItemList**)**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
