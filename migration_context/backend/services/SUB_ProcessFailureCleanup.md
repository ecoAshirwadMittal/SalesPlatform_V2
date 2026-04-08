# Microflow Detailed Specification: SUB_ProcessFailureCleanup

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)
- **$RMARequest_ImportHelperList** (Type: EcoATM_RMA.RMARequest_ImportHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_RMA.SUB_DeleteUploadHelpers****
2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.