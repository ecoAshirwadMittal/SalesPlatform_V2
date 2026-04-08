# Microflow Detailed Specification: ACT_ChangRMAStatus

### 📥 Inputs (Parameters)
- **$RMAUiHelper** (Type: EcoATM_RMA.RMAUiHelper)
- **$RMAMasterHelper** (Type: EcoATM_RMA.RMAMasterHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$RMAMasterHelper** (and Save to DB)
      - Set **RMAUiHelper_RMAMasterHelper_Selected** = `$RMAUiHelper`**
2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.