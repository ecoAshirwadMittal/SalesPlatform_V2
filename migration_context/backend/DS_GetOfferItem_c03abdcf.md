# Microflow Analysis: DS_GetOfferItem

### Requirements (Inputs):
- **$Device** (A record of type: EcoATM_PWSMDM.Device)
- **$Order** (A record of type: EcoATM_PWS.BuyerOffer)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.BuyerOfferItem** using filter: { [
  (
    EcoATM_PWS.BuyerOfferItem_Device = $Device
    and EcoATM_PWS.BuyerOfferItem_BuyerOffer = $Order
    and EcoATM_PWS.BuyerOfferItem_BuyerCode = $BuyerCode
  )
]
 } (Call this list **$OfferItem**)**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
