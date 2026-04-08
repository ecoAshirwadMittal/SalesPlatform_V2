# Microflow Detailed Specification: SUB_GetOrCreateBrand

### 📥 Inputs (Parameters)
- **$BrandList** (Type: EcoATM_MDM.Brand)
- **$BrandText** (Type: Variable)
- **$enum_BuyerCodeType** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/Brand = $BrandText and $currentObject/BuyercodeType = $enum_BuyerCodeType` (Result: **$ExistingBrand**)**
2. 🔀 **DECISION:** `$ExistingBrand != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$ExistingBrand`
   ➔ **If [false]:**
      1. **Create **EcoATM_MDM.Brand** (Result: **$NewBrand**)
      - Set **Brand** = `$BrandText`
      - Set **DisplayName** = `$BrandText`
      - Set **IsEnabledForFilter** = `true`
      - Set **Rank** = `0`
      - Set **BuyercodeType** = `$enum_BuyerCodeType`**
      2. **Add **$$NewBrand** to/from list **$BrandList****
      3. 🏁 **END:** Return `$NewBrand`

**Final Result:** This process concludes by returning a [Object] value.