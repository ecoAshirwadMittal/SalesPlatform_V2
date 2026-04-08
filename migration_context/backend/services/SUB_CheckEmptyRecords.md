# Microflow Detailed Specification: SUB_CheckEmptyRecords

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)
- **$RMAFile** (Type: EcoATM_RMA.RMAFile)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$RMARequest_ImportHelperList** (Type: EcoATM_RMA.RMARequest_ImportHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/IMEISerial_Number = empty or trim($currentObject/IMEISerial_Number) = ''` (Result: **$RMARequest_ImportHelper_EmptyReasonList**)**
2. 🔀 **DECISION:** `$RMARequest_ImportHelper_EmptyReasonList = empty`
   ➔ **If [false]:**
      1. **Update **$RMAFile** (and Save to DB)
      - Set **InvalidReason** = `length($RMARequest_ImportHelper_EmptyReasonList) + ' items are missing IMEIs, serial numbers or return reasons'`**
      2. 🏁 **END:** Return `length($RMARequest_ImportHelper_EmptyReasonList)`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `0`

**Final Result:** This process concludes by returning a [Integer] value.