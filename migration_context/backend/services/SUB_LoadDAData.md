# Microflow Detailed Specification: SUB_LoadDAData

### 📥 Inputs (Parameters)
- **$DAWeek** (Type: EcoATM_DA.DAWeek)

### ⚙️ Execution Flow (Logic Steps)
1. **ExecuteDatabaseQuery**
2. **List Operation: **Head** on **$undefined** (Result: **$NewDAUploadTime**)**
3. 🔀 **DECISION:** `$NewDAUploadTime/MAXUPLOADTIME != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$DAWeek/LastUploadTime=empty or parseDateTime($NewDAUploadTime/MAXUPLOADTIME, 'yyyy-MM-dd''T''HH:mm:ss')> $DAWeek/LastUploadTime`
         ➔ **If [false]:**
            1. **Update **$DAWeek** (and Save to DB)
      - Set **LastRefreshTime** = `[%CurrentDateTime%]`**
            2. 🏁 **END:** Return `empty`
         ➔ **If [true]:**
            1. **Retrieve related **DeviceAllocation_DAWeek** via Association from **$DAWeek** (Result: **$DeviceAllocationList_Existing**)**
            2. **Call Microflow **EcoATM_DA.SUB_GetDADataFromExternalDB****
            3. **Update **$DAWeek** (and Save to DB)
      - Set **LastUploadTime** = `if $NewDAUploadTime/MAXUPLOADTIME != empty then parseDateTime($NewDAUploadTime/MAXUPLOADTIME, 'yyyy-MM-dd''T''HH:mm:ss') else empty`
      - Set **LastRefreshTime** = `[%CurrentDateTime%]`**
            4. **Create Variable **$DeviceAllocationList_ExistingCount** = `1000`**
            5. 🔄 **LOOP:** For each **$undefined** in **$undefined**
               │ 1. **JavaCallAction**
               │ 2. **Remove **$$DeviceAllocationList_ExistingTop1000** to/from list **$DeviceAllocationList_Existing****
               │ 3. **AggregateList**
               │ 4. **Update Variable **$DeviceAllocationList_ExistingCount** = `$Top1000ListCount`**
               │ 5. **Delete**
               └─ **End Loop**
            6. **Call Microflow **Custom_Logging.SUB_Log_Info****
            7. 🏁 **END:** Return `empty`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `'We do not have device allocation data for week ' + $DAWeek/EcoATM_DA.DAWeek_Week/EcoATM_MDM.Week/WeekNumber + ' at the moment.'`

**Final Result:** This process concludes by returning a [String] value.