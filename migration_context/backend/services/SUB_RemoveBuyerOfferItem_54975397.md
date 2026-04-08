# Microflow Analysis: SUB_RemoveBuyerOfferItem

### Requirements (Inputs):
- **$BuyerOfferItem** (A record of type: EcoATM_PWS.BuyerOfferItem)
- **$BuyerOffer** (A record of type: EcoATM_PWS.BuyerOffer)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.BuyerOfferItem.Quantity] to: "0"**
2. **Run another process: "EcoATM_PWS.OCH_OrderItem_Cart"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
