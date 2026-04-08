# Microflow Analysis: DS_GetOrCreateOrderItem_CaseLot

### Requirements (Inputs):
- **$Device** (A record of type: EcoATM_PWSMDM.Device)
- **$BuyerOffer** (A record of type: EcoATM_PWS.BuyerOffer)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$CaseLot** (A record of type: EcoATM_PWSMDM.CaseLot)
- **$ENUM_BuyerOfferItemType** (A record of type: Object)

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.BuyerOfferItem** using filter: { [
  (
    EcoATM_PWS.BuyerOfferItem_Device = $Device
    and EcoATM_PWS.BuyerOfferItem_BuyerOffer = $BuyerOffer
    and EcoATM_PWS.BuyerOfferItem_BuyerCode= $BuyerCode
    and EcoATM_PWS.BuyerOfferItem_CaseLot=$CaseLot
  )
]
 } (Call this list **$BuyerOfferItem**)**
2. **Decision:** "OrderItem Exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.BuyerOfferItem.OfferPrice] to: "if $BuyerOfferItem/OfferPrice = empty or $BuyerOfferItem/OfferPrice = 0 or $BuyerOfferItem/Quantity = empty or $BuyerOfferItem/Quantity = 0
then
$Device/CurrentListPrice
else
$BuyerOfferItem/OfferPrice
"
      - Change [EcoATM_PWS.BuyerOfferItem.TotalPrice] to: "if $BuyerOfferItem/OfferPrice = empty or $BuyerOfferItem/OfferPrice = 0 or $BuyerOfferItem/Quantity = empty or $BuyerOfferItem/Quantity = 0
then
empty
else
$BuyerOfferItem/OfferPrice * $CaseLot/CaseLotSize
"
      - Change [EcoATM_PWS.BuyerOfferItem.CaseOfferTotalPrice] to: "if $BuyerOfferItem/OfferPrice = empty or $BuyerOfferItem/OfferPrice = 0 or $BuyerOfferItem/Quantity = empty or $BuyerOfferItem/Quantity = 0
then
empty
else
$BuyerOfferItem/OfferPrice * $CaseLot/CaseLotSize* $BuyerOfferItem/Quantity
"
      - Change [EcoATM_PWS.BuyerOfferItem.IsExceedQty] to: "$Device != empty
and
$CaseLot/CaseLotATPQty!= empty
and
$BuyerOfferItem/Quantity != empty
and
$BuyerOfferItem/Quantity > $CaseLot/CaseLotATPQty
and
$CaseLot/CaseLotATPQty<= 100"
      - Change [EcoATM_PWS.BuyerOfferItem._Type] to: "$ENUM_BuyerOfferItemType"
      - **Save:** This change will be saved to the database immediately.**
4. **Run another process: "EcoATM_PWS.CAL_BuyerOfferItem_CSSStyle"
      - Store the result in a new variable called **$CSSClass****
5. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.BuyerOfferItem.CSSClass] to: "''"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
