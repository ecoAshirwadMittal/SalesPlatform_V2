# Microflow Detailed Specification: SUB_DeviceBuyer_SetAwardedQty

### 📥 Inputs (Parameters)
- **$JsonString** (Type: Variable)
- **$DeviceAllocation** (Type: EcoATM_DA.DeviceAllocation)
- **$NPE_ClearingBid** (Type: EcoATM_DA.NPE_ClearingBid)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. 🔀 **DECISION:** `$JsonString != empty`
   ➔ **If [true]:**
      1. **Create Variable **$AllDevicesAwarded** = `false`**
      2. **Create Variable **$AvailableQuantity** = `$DeviceAllocation/AvailableQty`**
      3. **ImportXml**
      4. **Retrieve related **JsonObject_Root** via Association from **$Result** (Result: **$JsonObjectList**)**
      5. **CreateList**
      6. **DB Retrieve **EcoATM_DA.DeviceBuyer** Filter: `[ EcoATM_DA.DeviceBuyer_DeviceAllocation = $DeviceAllocation and BuyerCode = 'EB' ]` (Result: **$EBBuyer**)**
      7. 🔄 **LOOP:** For each **$IteratorJsonObject** in **$JsonObjectList**
         │ 1. **DB Retrieve **EcoATM_DA.DeviceBuyer** Filter: `[ EcoATM_DA.DeviceBuyer_DeviceAllocation = $DeviceAllocation and $IteratorJsonObject/BuyerCode = BuyerCode ]` (Result: **$DeviceBuyer**)**
         │ 2. 🔀 **DECISION:** `$DeviceBuyer != empty`
         │    ➔ **If [true]:**
         │       1. **Update **$DeviceBuyer**
      - Set **AwardedQty** = `$IteratorJsonObject/QuantityAllocated`**
         │       2. **Update Variable **$AvailableQuantity** = `$AvailableQuantity - $IteratorJsonObject/QuantityAllocated`**
         │       3. 🔀 **DECISION:** `$AllDevicesAwarded = false`
         │          ➔ **If [true]:**
         │             1. 🔀 **DECISION:** `$AvailableQuantity > 0`
         │                ➔ **If [false]:**
         │                   1. **Update **$DeviceBuyer**
      - Set **ClearingBid** = `true`**
         │                   2. **Update **$NPE_ClearingBid**
      - Set **ClearingBid** = `toString($DeviceBuyer/Bid)`**
         │                   3. **Add **$$DeviceBuyer
** to/from list **$DeviceBuyerListToCommit****
         │                ➔ **If [true]:**
         │                   1. **Add **$$DeviceBuyer
** to/from list **$DeviceBuyerListToCommit****
         │          ➔ **If [false]:**
         │             1. **Add **$$DeviceBuyer
** to/from list **$DeviceBuyerListToCommit****
         │    ➔ **If [false]:**
         └─ **End Loop**
      8. **Update **$EBBuyer**
      - Set **AwardedQty** = `$AvailableQuantity`**
      9. **Add **$$EBBuyer
** to/from list **$DeviceBuyerListToCommit****
      10. **Commit/Save **$DeviceBuyerListToCommit** to Database**
      11. **Call Microflow **Custom_Logging.SUB_Log_Info****
      12. 🏁 **END:** Return `$AllDevicesAwarded`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.