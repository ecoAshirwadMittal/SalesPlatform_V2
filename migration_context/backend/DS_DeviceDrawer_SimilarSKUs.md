# Microflow Detailed Specification: DS_DeviceDrawer_SimilarSKUs

### 📥 Inputs (Parameters)
- **$OfferDrawerHelper** (Type: EcoATM_PWS.OfferDrawerHelper)
- **$Device** (Type: EcoATM_PWSMDM.Device)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Retrieve related **Device_Brand** via Association from **$Device** (Result: **$Brand**)**
3. **Retrieve related **Device_Model** via Association from **$Device** (Result: **$Model**)**
4. **Retrieve related **Device_Carrier** via Association from **$Device** (Result: **$Carrier**)**
5. **Retrieve related **Device_Capacity** via Association from **$Device** (Result: **$Capacity**)**
6. **Retrieve related **Device_Grade** via Association from **$Device** (Result: **$Grade**)**
7. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Model = $Model and EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Brand = $Brand and EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Carrier = $Carrier and EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Capacity = $Capacity and EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade = $Grade and EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferSubmissionDate >= $OfferDrawerHelper/Last90Days ] [OfferDrawerStatus='Countered']` (Result: **$OfferItemList_PendingOrder_C**)**
8. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Model = $Model and EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Brand = $Brand and EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Carrier = $Carrier and EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Capacity = $Capacity and EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade = $Grade and EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferSubmissionDate >= $OfferDrawerHelper/Last90Days ] [OfferDrawerStatus='Accepted']` (Result: **$OfferItemList_PendingOrder_A**)**
9. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Model = $Model and EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Brand = $Brand and EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Carrier = $Carrier and EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Capacity = $Capacity and EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade = $Grade and EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferSubmissionDate >= $OfferDrawerHelper/Last90Days ] [OfferDrawerStatus='Sales_Review']` (Result: **$OfferItemList_PendingOrder_SR**)**
10. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Model = $Model and EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Brand = $Brand and EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Carrier = $Carrier and EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Capacity = $Capacity and EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade = $Grade and EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferSubmissionDate >= $OfferDrawerHelper/Last90Days ] [OfferDrawerStatus!='Sales_Review'] [OfferDrawerStatus!='Accepted'] [OfferDrawerStatus!='Countered']` (Result: **$OfferItemList_PendingOrder_O**)**
11. **CreateList**
12. **Add **$$OfferItemList_PendingOrder_SR** to/from list **$OfferItemList****
13. **Add **$$OfferItemList_PendingOrder_A** to/from list **$OfferItemList****
14. **Add **$$OfferItemList_PendingOrder_C** to/from list **$OfferItemList****
15. **Add **$$OfferItemList_PendingOrder_O** to/from list **$OfferItemList****
16. 🏁 **END:** Return `$OfferItemList`

**Final Result:** This process concludes by returning a [List] value.