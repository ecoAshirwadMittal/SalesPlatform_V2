# Microflow Detailed Specification: VAL_Device

### 📥 Inputs (Parameters)
- **$Device** (Type: EcoATM_PWSMDM.Device)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_PWS.SUB_Notes_RetrieveOrCreate** (Result: **$DeviceNote**)**
2. **Create Variable **$Valid** = `true`**
3. **Create Variable **$Notes** = `''`**
4. 🔀 **DECISION:** `$Device/CurrentListPrice != empty and $Device/CurrentListPrice > 0`
   ➔ **If [true]:**
      1. **Update Variable **$Notes** = `''`**
      2. 🔀 **DECISION:** `$Device/CurrentMinPrice != empty and $Device/CurrentMinPrice > 0 and $Device/CurrentListPrice >= $Device/CurrentMinPrice`
         ➔ **If [true]:**
            1. **Update Variable **$Notes** = `''`**
            2. **Update **$DeviceNote** (and Save to DB)
      - Set **Notes** = `$Notes`**
            3. 🔀 **DECISION:** `$Valid`
               ➔ **If [true]:**
                  1. **Commit/Save **$Device** to Database**
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update Variable **$Valid** = `false`**
            2. **Update Variable **$Notes** = `if $Notes = empty or trim($Notes) = '' then 'Invalid Min Price' else $Notes + ', Invalid Min Price'`**
            3. **Update **$DeviceNote** (and Save to DB)
      - Set **Notes** = `$Notes`**
            4. 🔀 **DECISION:** `$Valid`
               ➔ **If [true]:**
                  1. **Commit/Save **$Device** to Database**
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update Variable **$Valid** = `false`**
      2. **Update Variable **$Notes** = `'Invalid List Price'`**
      3. 🔀 **DECISION:** `$Device/CurrentMinPrice != empty and $Device/CurrentMinPrice > 0 and $Device/CurrentListPrice >= $Device/CurrentMinPrice`
         ➔ **If [true]:**
            1. **Update Variable **$Notes** = `''`**
            2. **Update **$DeviceNote** (and Save to DB)
      - Set **Notes** = `$Notes`**
            3. 🔀 **DECISION:** `$Valid`
               ➔ **If [true]:**
                  1. **Commit/Save **$Device** to Database**
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update Variable **$Valid** = `false`**
            2. **Update Variable **$Notes** = `if $Notes = empty or trim($Notes) = '' then 'Invalid Min Price' else $Notes + ', Invalid Min Price'`**
            3. **Update **$DeviceNote** (and Save to DB)
      - Set **Notes** = `$Notes`**
            4. 🔀 **DECISION:** `$Valid`
               ➔ **If [true]:**
                  1. **Commit/Save **$Device** to Database**
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.