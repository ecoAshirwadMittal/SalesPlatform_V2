# Microflow Detailed Specification: SUB_ProcessFileUpload

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)
- **$RMAFile** (Type: EcoATM_RMA.RMAFile)

### ⚙️ Execution Flow (Logic Steps)
1. **ImportExcelData**
2. 🔀 **DECISION:** `$RMARequest_ImportHelperList != empty`
   ➔ **If [false]:**
      1. **Update **$RMAFile** (and Save to DB)
      - Set **InvalidReason** = `'Invalid file. Zero IMEIs or serial numbers found.'`**
      2. 🏁 **END:** Return `empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$RMARequest_ImportHelperList`

**Final Result:** This process concludes by returning a [List] value.