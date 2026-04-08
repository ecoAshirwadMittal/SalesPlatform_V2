# Microflow Analysis: SUB_ReserveQuantityForDevice

### Requirements (Inputs):
- **$OfferItem** (A record of type: EcoATM_PWS.OfferItem)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Device****
2. **Create Variable**
3. **Run another process: "EcoATM_PWS.SUB_GenerateReservedQuantityForDevice"
      - Store the result in a new variable called **$TotalReservedQuantity****
4. **Update the **$undefined** (Object):
      - Change [EcoATM_PWSMDM.Device.ReservedQty] to: "if($TotalReservedQuantity>$Device/AvailableQty)
then $Device/AvailableQty
else round($TotalReservedQuantity)"
      - Change [EcoATM_PWSMDM.Device.ATPQty] to: "if($TotalReservedQuantity>$Device/AvailableQty)
then 0
else $Device/AvailableQty- round($TotalReservedQuantity)
"**
5. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.OfferItem.Reserved] to: "$ReservationNeeded"
      - Change [EcoATM_PWS.OfferItem.ReservedOn] to: "if($ReservationNeeded)
then [%CurrentDateTime%]
else empty"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
