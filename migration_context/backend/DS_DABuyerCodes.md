# Microflow Detailed Specification: DS_DABuyerCodes

### 📥 Inputs (Parameters)
- **$DeviceAllocation** (Type: EcoATM_DA.DeviceAllocation)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_DA.DeviceBuyer** Filter: `[EcoATM_DA.DeviceBuyer_DAWeek = $DAHelper/EcoATM_DA.DAHelper_DAWeek]` (Result: **$DeviceBuyerList**)**
2. 🏁 **END:** Return `$DeviceBuyerList`

**Final Result:** This process concludes by returning a [List] value.