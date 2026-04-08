# Microflow Detailed Specification: SUB_DeviceBuyer_SetClearingBid

### 📥 Inputs (Parameters)
- **$NPE_ClearingBid** (Type: EcoATM_DA.NPE_ClearingBid)
- **$DeviceAllocation** (Type: EcoATM_DA.DeviceAllocation)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_DA.DeviceBuyer** Filter: `[ EcoATM_DA.DeviceBuyer_DeviceAllocation = $DeviceAllocation and AwardedQty != 0 ]` (Result: **$AwardedBids**)**
2. **DB Retrieve **EcoATM_DA.DeviceBuyer** Filter: `[ EcoATM_DA.DeviceBuyer_DeviceAllocation = $DeviceAllocation and AwardedQty = 0 and QtyCap = empty ]` (Result: **$NonAwardedBids**)**
3. **List Operation: **Subtract** on **$undefined** (Result: **$NewDeviceBuyerList**)**
4. **List Operation: **Sort** on **$undefined** sorted by: Bid (Descending) (Result: **$NewDeviceBuyerList_2**)**
5. **List Operation: **Head** on **$undefined** (Result: **$NewDeviceBuyer**)**
6. 🔀 **DECISION:** `$NewDeviceBuyer != empty`
   ➔ **If [true]:**
      1. **Update **$NewDeviceBuyer** (and Save to DB)
      - Set **ClearingBid** = `true`**
      2. **Update **$NPE_ClearingBid**
      - Set **ClearingBid** = `toString($NewDeviceBuyer/Bid)`**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **DB Retrieve **EcoATM_DA.DeviceBuyer** Filter: `[ EcoATM_DA.DeviceBuyer_DeviceAllocation = $DeviceAllocation and BuyerCode = 'EB' ]` (Result: **$ExistingDeviceBuyer**)**
      2. **Update **$ExistingDeviceBuyer**
      - Set **ClearingBid** = `true`**
      3. **Update **$NPE_ClearingBid**
      - Set **ClearingBid** = `toString($ExistingDeviceBuyer/Bid)`**
      4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.