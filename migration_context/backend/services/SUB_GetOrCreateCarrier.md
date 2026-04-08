# Microflow Detailed Specification: SUB_GetOrCreateCarrier

### 📥 Inputs (Parameters)
- **$CarrierList** (Type: EcoATM_MDM.Carrier)
- **$CarrierText** (Type: Variable)
- **$enum_BuyerCodeType** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/Carrier= $CarrierText and $currentObject/BuyercodeType = $enum_BuyerCodeType` (Result: **$Existing**)**
2. 🔀 **DECISION:** `$Existing != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$Existing`
   ➔ **If [false]:**
      1. **Create **EcoATM_MDM.Carrier** (Result: **$NewModel**)
      - Set **Carrier** = `$CarrierText`
      - Set **DisplayName** = `$CarrierText`
      - Set **IsEnabledForFilter** = `true`
      - Set **Rank** = `0`
      - Set **BuyercodeType** = `$enum_BuyerCodeType`**
      2. **Add **$$NewModel** to/from list **$CarrierList****
      3. 🏁 **END:** Return `$NewModel`

**Final Result:** This process concludes by returning a [Object] value.