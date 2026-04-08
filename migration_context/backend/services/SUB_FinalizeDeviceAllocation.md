# Microflow Detailed Specification: SUB_FinalizeDeviceAllocation

### 📥 Inputs (Parameters)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)
- **$DAWeek** (Type: EcoATM_DA.DAWeek)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$DAWeek/IsFinalized = false`
   ➔ **If [true]:**
      1. **Maps to Page: **EcoATM_DA.DeviceAllocation_Finalize****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Information): `Device allocation for this week has already been finalized.`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.