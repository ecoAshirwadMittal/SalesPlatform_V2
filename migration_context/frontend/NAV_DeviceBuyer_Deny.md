# Microflow Detailed Specification: NAV_DeviceBuyer_Deny

### 📥 Inputs (Parameters)
- **$DAWeek** (Type: EcoATM_DA.DAWeek)
- **$NPE_ClearingBid** (Type: EcoATM_DA.NPE_ClearingBid)
- **$DeviceAllocation** (Type: EcoATM_DA.DeviceAllocation)
- **$DeviceBuyer** (Type: EcoATM_DA.DeviceBuyer)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Maps to Page: **EcoATM_DA.Popup_DeviceAllocation_Reject****
2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.