# Microflow Detailed Specification: SUB_DeleteAllDeviceAllocation_Admin

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Amount** = `500`**
2. **Create Variable **$Offset** = `0`**
3. **JavaCallAction**
4. **DB Retrieve **EcoATM_DA.DeviceAllocation**  (Result: **$DeviceAllocationList**)**
5. **AggregateList**
6. **Delete**
7. 🔀 **DECISION:** `$Count < $Amount`
   ➔ **If [true]:**
      1. **Show Message (Information): `Delete complete`**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **JavaCallAction**
         *(Merging with existing path logic)*

**Final Result:** This process concludes by returning a [Void] value.