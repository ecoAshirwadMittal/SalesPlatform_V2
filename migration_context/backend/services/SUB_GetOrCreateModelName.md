# Microflow Detailed Specification: SUB_GetOrCreateModelName

### 📥 Inputs (Parameters)
- **$ModelNameList** (Type: EcoATM_MDM.ModelName)
- **$ModelNameText** (Type: Variable)
- **$enum_BuyerCodeType** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/ModelName = $ModelNameText and $currentObject/BuyercodeType = $enum_BuyerCodeType` (Result: **$Existing**)**
2. 🔀 **DECISION:** `$Existing != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$Existing`
   ➔ **If [false]:**
      1. **Create **EcoATM_MDM.ModelName** (Result: **$NewModelName**)
      - Set **ModelName** = `$ModelNameText`
      - Set **DisplayName** = `$ModelNameText`
      - Set **IsEnabledForFilter** = `true`
      - Set **Rank** = `0`
      - Set **BuyercodeType** = `$enum_BuyerCodeType`**
      2. **Add **$$NewModelName** to/from list **$ModelNameList****
      3. 🏁 **END:** Return `$NewModelName`

**Final Result:** This process concludes by returning a [Object] value.