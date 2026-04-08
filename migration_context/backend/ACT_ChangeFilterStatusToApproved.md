# Microflow Detailed Specification: ACT_ChangeFilterStatusToApproved

### 📥 Inputs (Parameters)
- **$RMAFilterHelper** (Type: EcoATM_RMA.RMAFilterHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$RMAFilterHelper**
      - Set **FilterStatus** = `EcoATM_RMA.ENUM_RMAItemStatus.Approve`**
2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.