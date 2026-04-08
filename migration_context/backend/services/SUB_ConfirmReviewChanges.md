# Microflow Detailed Specification: SUB_ConfirmReviewChanges

### 📥 Inputs (Parameters)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)
- **$DAWeek** (Type: EcoATM_DA.DAWeek)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. 🔀 **DECISION:** `$DAWeek!=empty`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_DA.DeviceBuyer** Filter: `[EcoATM_DA.DeviceBuyer_DAWeek = $DAWeek] [IsChanged] [not(EB)]` (Result: **$ChangedDeviceBuyerList**)**
      2. 🔀 **DECISION:** `length($ChangedDeviceBuyerList) > 0`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_DA.SUB_SetIsChangedBidData_DA** (Result: **$BidDataList_Changed**)**
            2. 🔀 **DECISION:** `length($BidDataList_Changed) > 0`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_DA.SUB_TransferDADataToSharepoint****
                  2. **Call Microflow **EcoATM_DA.SUB_ResetBidDataAndDAData****
                  3. **Show Message (Information): `Your changes were successfully transferred to the processing portal.`**
                  4. **Call Microflow **Custom_Logging.SUB_Log_Info****
                  5. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.