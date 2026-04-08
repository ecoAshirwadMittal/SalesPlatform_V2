# Microflow Detailed Specification: ACT_FeatureFlag_RetrieveOrCreate

### 📥 Inputs (Parameters)
- **$FeatureFlag_String** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **Eco_Core.PWSFeatureFlag** Filter: `[ ( Name = $FeatureFlag_String ) ]` (Result: **$FeatureFlag**)**
2. 🔀 **DECISION:** `$FeatureFlag = empty`
   ➔ **If [true]:**
      1. **Create **Eco_Core.PWSFeatureFlag** (Result: **$NewFeatureFlag**)
      - Set **Name** = `$FeatureFlag_String`
      - Set **Active** = `false`**
      2. 🏁 **END:** Return `$NewFeatureFlag/Active`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$FeatureFlag/Active`

**Final Result:** This process concludes by returning a [Boolean] value.