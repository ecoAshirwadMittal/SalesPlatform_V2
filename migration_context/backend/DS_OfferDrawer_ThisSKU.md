# Microflow Detailed Specification: DS_OfferDrawer_ThisSKU

### 📥 Inputs (Parameters)
- **$OfferDrawerHelper** (Type: EcoATM_PWS.OfferDrawerHelper)
- **$OfferItem** (Type: EcoATM_PWS.OfferItem)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Retrieve related **OfferItem_Device** via Association from **$OfferItem** (Result: **$Device**)**
3. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferSubmissionDate >= $OfferDrawerHelper/Last90Days] [EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU= $Device/SKU] [OfferDrawerStatus='Sales_Review']` (Result: **$OfferItemList_PendingOrder_SalesReview**)**
4. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferSubmissionDate >= $OfferDrawerHelper/Last90Days] [EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU= $Device/SKU] [OfferDrawerStatus='Accepted']` (Result: **$OfferItemList_PendingOrder_Accepted**)**
5. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferSubmissionDate >= $OfferDrawerHelper/Last90Days] [EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU= $Device/SKU] [OfferDrawerStatus='Countered']` (Result: **$OfferItemList_PendingOrder_Countered**)**
6. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferSubmissionDate >= $OfferDrawerHelper/Last90Days] [EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU= $Device/SKU] [OfferDrawerStatus!='Countered'] [OfferDrawerStatus!='Accepted'] [OfferDrawerStatus!='Sales_Review']` (Result: **$OfferItemList_PendingOrder_OtherStatus**)**
7. **CreateList**
8. **Add **$$OfferItemList_PendingOrder_SalesReview** to/from list **$OfferItemList****
9. **Add **$$OfferItemList_PendingOrder_Accepted** to/from list **$OfferItemList****
10. **Add **$$OfferItemList_PendingOrder_Countered** to/from list **$OfferItemList****
11. **Add **$$OfferItemList_PendingOrder_OtherStatus** to/from list **$OfferItemList****
12. 🏁 **END:** Return `$OfferItemList`

**Final Result:** This process concludes by returning a [List] value.