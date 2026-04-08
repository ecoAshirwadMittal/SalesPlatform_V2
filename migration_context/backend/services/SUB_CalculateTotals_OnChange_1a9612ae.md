# Microflow Analysis: SUB_CalculateTotals_OnChange

### Requirements (Inputs):
- **$BuyerOfferItemList** (A record of type: EcoATM_PWS.BuyerOfferItem)
- **$LastBuyerOffer** (A record of type: EcoATM_PWS.BuyerOffer)

### Execution Steps:
1. **Take the list **$BuyerOfferItemList**, perform a [Filter] where: { empty }, and call the result **$BuyerOfferItemList_EmptyTypes****
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Take the list **$BuyerOfferItemList**, perform a [FilterByExpression] where: { $currentObject/TotalPrice != empty
and
$currentObject/TotalPrice > 0 }, and call the result **$BuyerOfferItemList_Valid****
4. **Take the list **$BuyerOfferItemList**, perform a [Filter] where: { true }, and call the result **$BuyerOfferItem_RedList_Qty****
5. **Take the list **$BuyerOfferItemList_Valid**, perform a [Filter] where: { EcoATM_PWS.ENUM_BuyerOfferItemType.Functional_Device }, and call the result **$BuyerOfferItemList_FunctionalDevices****
6. **Aggregate List
      - Store the result in a new variable called **$TotalSKUs_FunctionalDevices****
7. **Take the list **$BuyerOfferItemList_Valid**, perform a [Filter] where: { EcoATM_PWS.ENUM_BuyerOfferItemType.Functional_Case_Lot }, and call the result **$BuyerOfferItemList_SPB****
8. **Run another process: "EcoATM_PWS.SUB_BuyerOfferItem_CalculateCaseLotSKUs"
      - Store the result in a new variable called **$TotalSKUs_CaseLots****
9. **Take the list **$BuyerOfferItemList_Valid**, perform a [Filter] where: { EcoATM_PWS.ENUM_BuyerOfferItemType.Untested_Device }, and call the result **$BuyerOfferItemList_UntestedDevices****
10. **Aggregate List
      - Store the result in a new variable called **$TotalSKUs_UntestedDevices****
11. **Aggregate List
      - Store the result in a new variable called **$TotalQuantity_FunctionalDevices****
12. **Aggregate List
      - Store the result in a new variable called **$TotalQuantity_UntestedDevices****
13. **Aggregate List
      - Store the result in a new variable called **$TotalQuantity_SPB****
14. **Aggregate List
      - Store the result in a new variable called **$SumTotalPrice_FunctionalDevices****
15. **Aggregate List
      - Store the result in a new variable called **$SumTotalPrice_UntestedDevices****
16. **Aggregate List
      - Store the result in a new variable called **$SumTotalPrice_SPB****
17. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.BuyerOffer.OfferTotal] to: "$SumTotalPrice_FunctionalDevices + $SumTotalPrice_SPB + $SumTotalPrice_UntestedDevices"
      - Change [EcoATM_PWS.BuyerOffer.OfferSKUs] to: "round($TotalSKUs_FunctionalDevices + $TotalSKUs_CaseLots + $TotalSKUs_UntestedDevices)"
      - Change [EcoATM_PWS.BuyerOffer.OfferQuantity] to: "round($TotalQuantity_FunctionalDevices + $TotalQuantity_SPB + $TotalQuantity_UntestedDevices)"
      - Change [EcoATM_PWS.BuyerOffer.IsExceedingQty] to: "length($BuyerOfferItem_RedList_Qty) > 0"
      - **Save:** This change will be saved to the database immediately.**
18. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
