# Microflow Detailed Specification: SUB_UpdateReservedQuanityPerDevice

### 📥 Inputs (Parameters)
- **$DeviceList** (Type: EcoATM_PWSMDM.Device)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **CreateList**
3. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[Reserved] [EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus='Ordered'] [EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType='PWS']` (Result: **$OfferItemList**)**
4. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
   │ 1. **Retrieve related **OfferItem_Device** via Association from **$IteratorOfferItem** (Result: **$Device_OI**)**
   │ 2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject=$Device_OI` (Result: **$DeviceExistsInList**)**
   │ 3. 🔀 **DECISION:** `$DeviceExistsInList!=empty`
   │    ➔ **If [true]:**
   │       1. **Update **$IteratorOfferItem**
      - Set **Reserved** = `false`
      - Set **ReservedOn** = `empty`
      - Set **OrderSynced** = `true`**
   │       2. **Add **$$IteratorOfferItem
** to/from list **$OfferItemList_Filtered****
   │    ➔ **If [false]:**
   └─ **End Loop**
5. **Commit/Save **$OfferItemList** to Database**
6. 🔄 **LOOP:** For each **$Device** in **$DeviceList**
   │ 1. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_Device=$Device] [Reserved]` (Result: **$OfferItemList_1**)**
   │ 2. **List Operation: **Subtract** on **$undefined** (Result: **$OfferItemList_ValidForCalculation**)**
   │ 3. **AggregateList**
   │ 4. **Update **$Device**
      - Set **ReservedQty** = `if($TotalReservedQuantity>$Device/AvailableQty) then $Device/AvailableQty else round($TotalReservedQuantity)`
      - Set **ATPQty** = `if($TotalReservedQuantity>$Device/AvailableQty) then 0 else $Device/AvailableQty- round($TotalReservedQuantity)`**
   └─ **End Loop**
7. **Commit/Save **$DeviceList** to Database**
8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.