# Microflow Detailed Specification: SUB_GetOrCreateModel

### 📥 Inputs (Parameters)
- **$ModelList** (Type: EcoATM_MDM.Model)
- **$ModelText** (Type: Variable)
- **$enum_BuyerCodeType** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/Model = $ModelText and $currentObject/BuyercodeType = $enum_BuyerCodeType` (Result: **$Existing**)**
2. 🔀 **DECISION:** `$Existing != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$Existing`
   ➔ **If [false]:**
      1. **Create **EcoATM_MDM.Model** (Result: **$NewModel**)
      - Set **Model** = `$ModelText`
      - Set **DisplayName** = `$ModelText`
      - Set **IsEnabledForFilter** = `true`
      - Set **Rank** = `0`
      - Set **BuyercodeType** = `$enum_BuyerCodeType`**
      2. **Add **$$NewModel** to/from list **$ModelList****
      3. 🏁 **END:** Return `$NewModel`

**Final Result:** This process concludes by returning a [Object] value.