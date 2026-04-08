# Microflow Detailed Specification: ACT_DeviceAllocation_ChangeStatus

### 📥 Inputs (Parameters)
- **$DeviceBuyer** (Type: EcoATM_DA.DeviceBuyer)
- **$Accept** (Type: Variable)
- **$NPE_ClearingBid** (Type: EcoATM_DA.NPE_ClearingBid)
- **$DeviceAllocation** (Type: EcoATM_DA.DeviceAllocation)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Maps to Page: **EcoATM_DA.Popup_DeviceAllocation_Reject****
2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.