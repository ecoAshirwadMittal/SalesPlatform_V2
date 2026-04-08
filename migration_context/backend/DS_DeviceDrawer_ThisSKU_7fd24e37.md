# Microflow Analysis: DS_DeviceDrawer_ThisSKU

### Requirements (Inputs):
- **$OfferDrawerHelper** (A record of type: EcoATM_PWS.OfferDrawerHelper)
- **$Device** (A record of type: EcoATM_PWSMDM.Device)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferSubmissionDate >= $OfferDrawerHelper/Last90Days]
[EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU= $Device/SKU]
[OfferDrawerStatus='Sales_Review'] } (Call this list **$OfferItemList_PendingOrder_SalesReview**)**
3. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferSubmissionDate >= $OfferDrawerHelper/Last90Days]
[EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU= $Device/SKU]
[OfferDrawerStatus='Accepted'] } (Call this list **$OfferItemList_PendingOrder_Accepted**)**
4. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferSubmissionDate >= $OfferDrawerHelper/Last90Days]
[EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU= $Device/SKU]
[OfferDrawerStatus='Countered'] } (Call this list **$OfferItemList_PendingOrder_Countered**)**
5. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferSubmissionDate >= $OfferDrawerHelper/Last90Days]
[EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU= $Device/SKU]
[OfferDrawerStatus!='Countered']
[OfferDrawerStatus!='Accepted']
[OfferDrawerStatus!='Sales_Review'] } (Call this list **$OfferItemList_PendingOrder_OtherStatus**)**
6. **Create List
      - Store the result in a new variable called **$OfferItemList****
7. **Change List**
8. **Change List**
9. **Change List**
10. **Change List**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
