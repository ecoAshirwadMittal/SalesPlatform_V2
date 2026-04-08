# Microflow Analysis: SUB_ReserveQuantityForCaseLot

### Requirements (Inputs):
- **$OfferItem** (A record of type: EcoATM_PWS.OfferItem)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$CaseLot****
2. **Create Variable**
3. **Run another process: "EcoATM_PWS.SUB_GenerateReservedQuantityForCaseLot"
      - Store the result in a new variable called **$TotalReservedQuantity_CaseLot****
4. **Update the **$undefined** (Object):
      - Change [EcoATM_PWSMDM.CaseLot.CaseLotReservedQty] to: "if($TotalReservedQuantity_CaseLot>$CaseLot/CaseLotAvlQty)
then $CaseLot/CaseLotAvlQty
else round($TotalReservedQuantity_CaseLot)
"
      - Change [EcoATM_PWSMDM.CaseLot.CaseLotATPQty] to: "if($TotalReservedQuantity_CaseLot>$CaseLot/CaseLotAvlQty)
then 0
else $CaseLot/CaseLotAvlQty- round($TotalReservedQuantity_CaseLot)
"**
5. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.OfferItem.Reserved] to: "$ReservationNeeded_CaseLot
"
      - Change [EcoATM_PWS.OfferItem.ReservedOn] to: "if($ReservationNeeded_CaseLot)
then [%CurrentDateTime%]
else empty
"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
