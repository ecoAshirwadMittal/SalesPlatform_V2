# Microflow Detailed Specification: SUB_Note_GetOrCreate

### 📥 Inputs (Parameters)
- **$Note** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWSMDM.Note** Filter: `[Notes=$Note]` (Result: **$TargetNote**)**
2. 🔀 **DECISION:** `$TargetNote!=empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$TargetNote`
   ➔ **If [false]:**
      1. **Create **EcoATM_PWSMDM.Note** (Result: **$NewNote**)
      - Set **Notes** = `$Note`**
      2. 🏁 **END:** Return `$NewNote`

**Final Result:** This process concludes by returning a [Object] value.