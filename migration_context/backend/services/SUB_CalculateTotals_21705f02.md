# Microflow Analysis: SUB_CalculateTotals

### Requirements (Inputs):
- **$OrderItemList** (A record of type: EcoATM_PWS.BuyerOfferItem)
- **$LastBuyerOffer** (A record of type: EcoATM_PWS.BuyerOffer)

### Execution Steps:
1. **Take the list **$OrderItemList**, perform a [Filter] where: { true }, and call the result **$BuyerOfferItem_RedList_Qty****
2. **Aggregate List
      - Store the result in a new variable called **$TotalQuantity****
3. **Aggregate List
      - Store the result in a new variable called **$TotalSKUs****
4. **Aggregate List
      - Store the result in a new variable called **$SumTotalPrice****
5. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.BuyerOffer.OfferTotal] to: "$SumTotalPrice
"
      - Change [EcoATM_PWS.BuyerOffer.OfferSKUs] to: "round($TotalSKUs)"
      - Change [EcoATM_PWS.BuyerOffer.OfferQuantity] to: "round($TotalQuantity)"
      - Change [EcoATM_PWS.BuyerOffer.IsExceedingQty] to: "length($BuyerOfferItem_RedList_Qty) > 0"
      - **Save:** This change will be saved to the database immediately.**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
