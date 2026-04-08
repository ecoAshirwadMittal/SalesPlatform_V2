# Microflow Detailed Specification: SUB_DisableInactiveDevices

### 📥 Inputs (Parameters)
- **$DeviceList_Inactive** (Type: EcoATM_PWSMDM.Device)

### ⚙️ Execution Flow (Logic Steps)
1. **CreateList**
2. 🔄 **LOOP:** For each **$IteratorDevice** in **$DeviceList_Inactive**
   │ 1. **Update **$IteratorDevice**
      - Set **IsActive** = `false`
      - Set **LastUpdateDate** = `[%CurrentDateTime%]`**
   │ 2. **Retrieve related **BuyerOfferItem_Device** via Association from **$IteratorDevice** (Result: **$BuyerOfferItemList_Iterator**)**
   │ 3. **Add **$$BuyerOfferItemList_Iterator
** to/from list **$BuyerOfferItemList****
   └─ **End Loop**
3. **AggregateList**
4. **Call Microflow **Custom_Logging.SUB_Log_Info****
5. **Commit/Save **$DeviceList_Inactive** to Database**
6. **AggregateList**
7. **Call Microflow **Custom_Logging.SUB_Log_Info****
8. **Delete**
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.