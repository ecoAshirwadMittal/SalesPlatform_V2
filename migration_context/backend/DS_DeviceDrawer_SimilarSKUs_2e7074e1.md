# Microflow Analysis: DS_DeviceDrawer_SimilarSKUs

### Requirements (Inputs):
- **$OfferDrawerHelper** (A record of type: EcoATM_PWS.OfferDrawerHelper)
- **$Device** (A record of type: EcoATM_PWSMDM.Device)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Retrieve
      - Store the result in a new variable called **$Brand****
3. **Retrieve
      - Store the result in a new variable called **$Model****
4. **Retrieve
      - Store the result in a new variable called **$Carrier****
5. **Retrieve
      - Store the result in a new variable called **$Capacity****
6. **Retrieve
      - Store the result in a new variable called **$Grade****
7. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Model = $Model
and
EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Brand = $Brand
and
EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Carrier = $Carrier
and
EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Capacity = $Capacity
and
EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade = $Grade
and
EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferSubmissionDate >= $OfferDrawerHelper/Last90Days
]
[OfferDrawerStatus='Countered']
 } (Call this list **$OfferItemList_PendingOrder_C**)**
8. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Model = $Model
and
EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Brand = $Brand
and
EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Carrier = $Carrier
and
EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Capacity = $Capacity
and
EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade = $Grade
and
EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferSubmissionDate >= $OfferDrawerHelper/Last90Days
]
[OfferDrawerStatus='Accepted']
 } (Call this list **$OfferItemList_PendingOrder_A**)**
9. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Model = $Model
and
EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Brand = $Brand
and
EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Carrier = $Carrier
and
EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Capacity = $Capacity
and
EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade = $Grade
and
EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferSubmissionDate >= $OfferDrawerHelper/Last90Days
]
[OfferDrawerStatus='Sales_Review']
 } (Call this list **$OfferItemList_PendingOrder_SR**)**
10. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Model = $Model
and
EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Brand = $Brand
and
EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Carrier = $Carrier
and
EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Capacity = $Capacity
and
EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade = $Grade
and
EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferSubmissionDate >= $OfferDrawerHelper/Last90Days
]
[OfferDrawerStatus!='Sales_Review']
[OfferDrawerStatus!='Accepted']
[OfferDrawerStatus!='Countered']
 } (Call this list **$OfferItemList_PendingOrder_O**)**
11. **Create List
      - Store the result in a new variable called **$OfferItemList****
12. **Change List**
13. **Change List**
14. **Change List**
15. **Change List**
16. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
