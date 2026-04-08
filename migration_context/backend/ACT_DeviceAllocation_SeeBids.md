# Microflow Detailed Specification: ACT_DeviceAllocation_SeeBids

### 📥 Inputs (Parameters)
- **$DeviceAllocationList** (Type: EcoATM_DA.DeviceAllocation)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)
- **$CurrentObjectDeviceAllocation** (Type: EcoATM_DA.DeviceAllocation)

### ⚙️ Execution Flow (Logic Steps)
1. **AggregateList**
2. 🔀 **DECISION:** `$CountDeviceAllocationList >= 2`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$CountDeviceAllocationList <= 50`
         ➔ **If [true]:**
            1. **Maps to Page: **EcoATM_DA.PG_DeviceAllocationReviewAll****
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Maps to Page: **EcoATM_DA.PG_DeviceAllocationReviewAll****
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$CurrentObjectDeviceAllocation = empty`
         ➔ **If [true]:**
            1. **Show Message (Information): `No object selected!`**
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Maps to Page: **EcoATM_DA.PG_DeviceAllocationReview****
            2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.