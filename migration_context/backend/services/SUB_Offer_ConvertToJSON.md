# Microflow Detailed Specification: SUB_Offer_ConvertToJSON

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Offer!=empty`
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      2. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
      3. **CreateList**
      4. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
         │ 1. 🔀 **DECISION:** `$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType='SPB'`
         │    ➔ **If [true]:**
         │       1. **Create **EcoATM_PWSMDM.DeviceSFTemp** (Result: **$NewDeviceSFTemp**)
      - Set **SKU** = `$IteratorOfferItem/EcoATM_PWS.OfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotID`
      - Set **AvlQty** = `$IteratorOfferItem/EcoATM_PWS.OfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotAvlQty`
      - Set **ATPQty** = `$IteratorOfferItem/EcoATM_PWS.OfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotAvlQty`
      - Set **ReservedQty** = `$IteratorOfferItem/EcoATM_PWS.OfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotReservedQty`**
         │       2. **Add **$$NewDeviceSFTemp** to/from list **$DeviceSFTempList****
         │       3. **Update **$IteratorOfferItem**
      - Set **OfferItem_DeviceSFTemp** = `$NewDeviceSFTemp`**
         │    ➔ **If [false]:**
         │       1. **Create **EcoATM_PWSMDM.DeviceSFTemp** (Result: **$NewDeviceSFTemp_1**)
      - Set **SKU** = `$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU`
      - Set **AvlQty** = `$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/AvailableQty`
      - Set **ATPQty** = `$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ATPQty`
      - Set **ReservedQty** = `$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ReservedQty`**
         │       2. **Add **$$NewDeviceSFTemp_1** to/from list **$DeviceSFTempList****
         │       3. **Update **$IteratorOfferItem**
      - Set **OfferItem_DeviceSFTemp** = `$NewDeviceSFTemp_1`**
         └─ **End Loop**
      5. **ExportXml**
      6. **Delete**
      7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      8. 🏁 **END:** Return `$JSONContent`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [String] value.