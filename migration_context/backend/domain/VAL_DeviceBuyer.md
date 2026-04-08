# Microflow Detailed Specification: VAL_DeviceBuyer

### 📥 Inputs (Parameters)
- **$DeviceBuyer** (Type: EcoATM_DA.DeviceBuyer)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$AlwaysTrue** = `true`**
2. 🔀 **DECISION:** `$DeviceBuyer/BuyerCode != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$DeviceBuyer/Bid != empty`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$AlwaysTrue`
         ➔ **If [false]:**
            1. **Show Message (Information): `No bid found!`**
            2. **Update Variable **$AlwaysTrue** = `false`**
            3. 🏁 **END:** Return `$AlwaysTrue`
   ➔ **If [false]:**
      1. **Show Message (Information): `No Buyer Code found!`**
      2. **Update Variable **$AlwaysTrue** = `false`**
      3. 🔀 **DECISION:** `$DeviceBuyer/Bid != empty`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$AlwaysTrue`
         ➔ **If [false]:**
            1. **Show Message (Information): `No bid found!`**
            2. **Update Variable **$AlwaysTrue** = `false`**
            3. 🏁 **END:** Return `$AlwaysTrue`

**Final Result:** This process concludes by returning a [Boolean] value.