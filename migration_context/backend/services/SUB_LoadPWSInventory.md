# Microflow Detailed Specification: SUB_LoadPWSInventory

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Create Variable **$StartTime** = `[%CurrentDateTime%]`**
3. **Create Variable **$PWSDeviceCount** = `@EcoATM_PWSIntegration.CONST_SF_QueryPageSize`**
4. **Call Microflow **EcoATM_PWS.DS_GetOrCreateMDMFuturePriceHelper** (Result: **$MDMFuturePriceHelper**)**
5. **Create Variable **$Limit** = `$PWSDeviceCount`**
6. **Create Variable **$Offset** = `0`**
7. 🔄 **LOOP:** For each **$undefined** in **$undefined**
   │ 1. **Call Microflow **EcoATM_PWS.SUB_LoadPWSInventory_Task** (Result: **$DeviceCount**)**
   │ 2. **Update Variable **$Offset** = `$Offset + $Limit`**
   │ 3. **Update Variable **$PWSDeviceCount** = `if $DeviceCount != empty then $DeviceCount else $Limit`**
   └─ **End Loop**
8. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[changedDate < $StartTime]` (Result: **$DeviceList_Inactive**)**
9. **CreateList**
10. 🔄 **LOOP:** For each **$IteratorDevice** in **$DeviceList_Inactive**
   │ 1. **Update **$IteratorDevice**
      - Set **IsActive** = `false`**
   │ 2. **Retrieve related **BuyerOfferItem_Device** via Association from **$IteratorDevice** (Result: **$BuyerOfferItemList_Iterator**)**
   │ 3. **Add **$$BuyerOfferItemList_Iterator
** to/from list **$BuyerOfferItemList****
   └─ **End Loop**
11. **Commit/Save **$DeviceList_Inactive** to Database**
12. **Delete**
13. **Call Microflow **EcoATM_PWS.SUB_ProcessAvailableQuantityForFilters****
14. **Call Microflow **EcoATM_PWS.SUB_CleanupMetaData****
15. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
16. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.