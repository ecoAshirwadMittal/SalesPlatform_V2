# Microflow Detailed Specification: SUB_Notes_RetrieveOrCreate

### 📥 Inputs (Parameters)
- **$Device** (Type: EcoATM_PWSMDM.Device)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Device_Note** via Association from **$Device** (Result: **$Note**)**
2. 🔀 **DECISION:** `$Note != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$Note`
   ➔ **If [false]:**
      1. **Create **EcoATM_PWSMDM.Note** (Result: **$NewNote**)**
      2. **Update **$Device**
      - Set **Device_Note** = `$NewNote`**
      3. 🏁 **END:** Return `$NewNote`

**Final Result:** This process concludes by returning a [Object] value.