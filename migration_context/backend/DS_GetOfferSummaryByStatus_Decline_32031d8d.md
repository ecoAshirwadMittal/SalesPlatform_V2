# Microflow Analysis: DS_GetOfferSummaryByStatus_Decline

### Requirements (Inputs):
- **$OfferMasterHelper** (A record of type: EcoATM_PWS.OfferMasterHelper)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Search the Database for **EcoATM_PWS.Offer** using filter: { [OfferStatus = 'Declined'] } (Call this list **$DeclinedOfferList**)**
3. **Aggregate List
      - Store the result in a new variable called **$Count****
4. **Aggregate List
      - Store the result in a new variable called **$TotalSKU****
5. **Aggregate List
      - Store the result in a new variable called **$TotalQuantity****
6. **Aggregate List
      - Store the result in a new variable called **$TotalPrice****
7. **Run another process: "EcoATM_PWS.SUB_GetOrCreateOfferUiHelper"
      - Store the result in a new variable called **$OffersUiHelper****
8. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.OffersUiHelper.OfferStatus] to: "EcoATM_PWS.ENUM_PWSOrderStatus.Declined
"
      - Change [EcoATM_PWS.OffersUiHelper.TotalSKUs] to: "$TotalSKU"
      - Change [EcoATM_PWS.OffersUiHelper.TotalQty] to: "round($TotalQuantity)
"
      - Change [EcoATM_PWS.OffersUiHelper.TotalPrice] to: "round($TotalPrice)"
      - Change [EcoATM_PWS.OffersUiHelper_OfferMasterHelper] to: "$OfferMasterHelper"
      - Change [EcoATM_PWS.OffersUiHelper.OfferCount] to: "$Count"
      - **Save:** This change will be saved to the database immediately.**
9. **Run another process: "Custom_Logging.SUB_Log_Info"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
