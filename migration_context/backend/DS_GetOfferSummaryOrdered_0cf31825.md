# Microflow Analysis: DS_GetOfferSummaryOrdered

### Requirements (Inputs):
- **$ENUM_PWSOrderStatus** (A record of type: Object)
- **$OfferMasterHelper** (A record of type: EcoATM_PWS.OfferMasterHelper)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Search the Database for **EcoATM_PWS.Offer** using filter: { [OfferStatus = 'Ordered' or OfferStatus = 'Pending_Order'] } (Call this list **$OfferList**)**
3. **Aggregate List
      - Store the result in a new variable called **$TotalOffers****
4. **Aggregate List
      - Store the result in a new variable called **$TotalSKUs****
5. **Aggregate List
      - Store the result in a new variable called **$TotalQty****
6. **Aggregate List
      - Store the result in a new variable called **$TotalPrice****
7. **Run another process: "EcoATM_PWS.SUB_GetOrCreateOfferUiHelper"
      - Store the result in a new variable called **$OffersUiHelper****
8. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.OffersUiHelper.OfferStatus] to: "$ENUM_PWSOrderStatus
"
      - Change [EcoATM_PWS.OffersUiHelper.OfferCount] to: "$TotalOffers
"
      - Change [EcoATM_PWS.OffersUiHelper.TotalSKUs] to: "$TotalSKUs
"
      - Change [EcoATM_PWS.OffersUiHelper.TotalQty] to: "$TotalQty
"
      - Change [EcoATM_PWS.OffersUiHelper.TotalPrice] to: "$TotalPrice
"
      - Change [EcoATM_PWS.OffersUiHelper_OfferMasterHelper] to: "$OfferMasterHelper"
      - **Save:** This change will be saved to the database immediately.**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
