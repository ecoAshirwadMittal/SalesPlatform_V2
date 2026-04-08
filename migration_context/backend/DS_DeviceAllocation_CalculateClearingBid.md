# Microflow Detailed Specification: DS_DeviceAllocation_CalculateClearingBid

### 📥 Inputs (Parameters)
- **$DeviceAllocation** (Type: EcoATM_DA.DeviceAllocation)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Retrieve related **DeviceBuyer_DeviceAllocation** via Association from **$DeviceAllocation** (Result: **$DeviceBuyerList**)**
3. **Create **EcoATM_DA.NPE_ClearingBid** (Result: **$NewNPE_ClearingBid**)**
4. 🔀 **DECISION:** `$DeviceBuyerList != empty`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_DA.DeviceBuyer** Filter: `[ EcoATM_DA.DeviceBuyer_DeviceAllocation = $DeviceAllocation and ClearingBid = true ]` (Result: **$ClearingBidDevuceBuyer**)**
      2. 🔀 **DECISION:** `$ClearingBidDevuceBuyer != empty`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_DA.SUB_DeviceBuyer_CalculateLastAwardedBid****
            2. **Call Microflow **Custom_Logging.SUB_Log_Info****
            3. 🏁 **END:** Return `$NewNPE_ClearingBid`
         ➔ **If [false]:**
            1. **Call Microflow **EcoATM_DA.SUB_DeviceBuyer_SetClearingBid****
            2. **Call Microflow **EcoATM_DA.SUB_DeviceBuyer_CalculateLastAwardedBid****
            3. **Call Microflow **Custom_Logging.SUB_Log_Info****
            4. 🏁 **END:** Return `$NewNPE_ClearingBid`
   ➔ **If [false]:**
      1. **Retrieve related **DeviceAllocation_DAWeek** via Association from **$DeviceAllocation** (Result: **$DAWeek**)**
      2. **Retrieve related **DAWeek_Week** via Association from **$DAWeek** (Result: **$Week**)**
      3. **Call Microflow **EcoATM_DA.SUB_GetUniqueBidDataList** (Result: **$BidDataList**)**
      4. **Call Microflow **EcoATM_DA.SUB_DeviceAllocation_CreateDeviceBuyers** (Result: **$UpdatedDeviceBuyerList**)**
      5. **Call Microflow **EcoATM_DA.SUB_DeviceBuyer_SetAwardedQty** (Result: **$ClearingBIdSet**)**
      6. 🔀 **DECISION:** `$ClearingBIdSet = true`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_DA.SUB_DeviceBuyer_CalculateLastAwardedBid****
            2. **Call Microflow **Custom_Logging.SUB_Log_Info****
            3. 🏁 **END:** Return `$NewNPE_ClearingBid`
         ➔ **If [false]:**
            1. **Call Microflow **EcoATM_DA.SUB_DeviceBuyer_SetClearingBid****
            2. **Call Microflow **EcoATM_DA.SUB_DeviceBuyer_CalculateLastAwardedBid****
            3. **Call Microflow **Custom_Logging.SUB_Log_Info****
            4. 🏁 **END:** Return `$NewNPE_ClearingBid`

**Final Result:** This process concludes by returning a [Object] value.