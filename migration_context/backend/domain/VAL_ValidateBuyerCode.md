# Microflow Detailed Specification: VAL_ValidateBuyerCode

### 📥 Inputs (Parameters)
- **$BuyerCode_Helper** (Type: EcoATM_BuyerManagement.BuyerCode_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$isValid** = `true`**
2. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[ ( Code = $BuyerCode_Helper/Code ) ]` (Result: **$BuyerCodeList_Existing**)**
3. **List Operation: **Head** on **$undefined** (Result: **$ExistingBuyerCode**)**
4. 🔀 **DECISION:** `$ExistingBuyerCode != empty`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$isValid`
   ➔ **If [true]:**
      1. **Show Message (Warning): `Duplicate buyer code: {1} Buyer code must be unique.`**
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.