# Microflow Detailed Specification: VAL_ValidateBuyerCode_PreSave

### 📥 Inputs (Parameters)
- **$NewBuyerHelper** (Type: EcoATM_BuyerManagement.NewBuyerHelper)
- **$NewCode** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$isValid** = `true`**
2. **Retrieve related **NewBuyerCodeHelper_NewBuyerHelper** via Association from **$NewBuyerHelper** (Result: **$BuyerCodeList**)**
3. **List Operation: **Filter** on **$undefined** where `$NewCode` (Result: **$NewBuyerCodeList**)**
4. **List Operation: **Head** on **$undefined** (Result: **$NewBuyerCode**)**
5. 🔀 **DECISION:** `$NewBuyerCode = empty`
   ➔ **If [false]:**
      1. **Show Message (Warning): `Duplicate buyer code already entered: {1}`**
      2. 🏁 **END:** Return `false`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[ ( Code = $NewCode ) ]` (Result: **$BuyerCodeList_Existing**)**
      2. **List Operation: **Head** on **$undefined** (Result: **$ExistingBuyerCode**)**
      3. 🔀 **DECISION:** `$ExistingBuyerCode != empty`
         ➔ **If [true]:**
            1. **Show Message (Warning): `Duplicate buyer code: {1} Buyer code must be unique.`**
            2. 🏁 **END:** Return `false`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `$isValid`

**Final Result:** This process concludes by returning a [Boolean] value.