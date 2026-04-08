# Microflow Detailed Specification: DS_CreateRMAFile

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **EcoATM_RMA.RMAFile** (Result: **$NewRMAFile**)
      - Set **RMAFile_RMA** = `$RMA`**
2. 🏁 **END:** Return `$NewRMAFile`

**Final Result:** This process concludes by returning a [Object] value.