# Microflow Detailed Specification: ACT_DeviceAllocation_Commit

### 📥 Inputs (Parameters)
- **$DeviceAllocation** (Type: EcoATM_DA.DeviceAllocation)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Commit/Save **$DeviceAllocation** to Database**
2. **Maps to Page: **EcoATM_DA.PG_DeviceAllocationReview****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.