# Microflow Analysis: DS_OfferDrawer_SimilarSKUs

### Requirements (Inputs):
- **$OfferDrawerHelper** (A record of type: EcoATM_PWS.OfferDrawerHelper)
- **$OfferItem** (A record of type: EcoATM_PWS.OfferItem)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Retrieve
      - Store the result in a new variable called **$Device****
3. **Retrieve
      - Store the result in a new variable called **$Brand****
4. **Retrieve
      - Store the result in a new variable called **$Model****
5. **Retrieve
      - Store the result in a new variable called **$Carrier****
6. **Retrieve
      - Store the result in a new variable called **$Capacity****
7. **Retrieve
      - Store the result in a new variable called **$Grade****
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
[OfferDrawerStatus='Countered']
 } (Call this list **$OfferItemList_PendingOrder_C**)**
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
[OfferDrawerStatus='Accepted']
 } (Call this list **$OfferItemList_PendingOrder_A**)**
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
[OfferDrawerStatus='Sales_Review']
 } (Call this list **$OfferItemList_PendingOrder_SR**)**
11. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Model = $Model
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
12. **Create List
      - Store the result in a new variable called **$OfferItemList****
13. **Change List**
14. **Change List**
15. **Change List**
16. **Change List**
17. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
