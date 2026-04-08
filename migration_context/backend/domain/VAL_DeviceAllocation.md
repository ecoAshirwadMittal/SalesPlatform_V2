# Microflow Detailed Specification: VAL_DeviceAllocation

### 📥 Inputs (Parameters)
- **$DeviceAllocation** (Type: EcoATM_DA.DeviceAllocation)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$DeviceAllocation/ProductID != empty`
   ➔ **If [true]:**
      1. **Create Variable **$AlwaysTrue** = `true`**
      2. 🏁 **END:** Return `$AlwaysTrue`
   ➔ **If [false]:**
      1. **Show Message (Information): `Invalid Device allocation`**
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.