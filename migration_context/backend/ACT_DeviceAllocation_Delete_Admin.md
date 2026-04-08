# Microflow Detailed Specification: ACT_DeviceAllocation_Delete_Admin

### 📥 Inputs (Parameters)
- **$DAWeek** (Type: EcoATM_DA.DAWeek)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **DB Retrieve **EcoATM_DA.DeviceAllocation** Filter: `[EcoATM_DA.DeviceAllocation_DAWeek = $DAWeek]` (Result: **$DeviceAllocationList**)**
3. 🔀 **DECISION:** `$DeviceAllocationList !=empty`
   ➔ **If [true]:**
      1. **Delete**
         *(Merging with existing path logic)*
   ➔ **If [false]:**
      1. **Delete**
      2. **Call Microflow **Custom_Logging.SUB_Log_Info****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.