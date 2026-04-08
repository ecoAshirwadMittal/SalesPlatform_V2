# Microflow Detailed Specification: VAL_Item_IsValid

### 📥 Inputs (Parameters)
- **$Item** (Type: EcoATM_PWSIntegration.Item)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Item!=empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `trim($Item/ItemType)!=''`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `trim($Item/Sku)!=''`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `trim($Item/Title)!=''`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `empty`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `'Title is missing. Check that all fields that define a device are correctly filled in.'`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `'SKU is missing. Check that all fields that define a device are correctly filled in.'`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `'ItemType is missing - Check that all fields that define a device are correctly filled in.'`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `'Device data is missing'`

**Final Result:** This process concludes by returning a [String] value.