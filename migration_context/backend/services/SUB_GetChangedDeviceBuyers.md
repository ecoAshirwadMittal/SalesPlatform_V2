# Microflow Detailed Specification: SUB_GetChangedDeviceBuyers

### 📥 Inputs (Parameters)
- **$DAWeek** (Type: EcoATM_DA.DAWeek)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_DA.DeviceBuyer** Filter: `[EcoATM_DA.DeviceBuyer_DAWeek = $DAWeek] [IsChanged] [not(EB)]` (Result: **$DeviceBuyerList**)**
2. 🏁 **END:** Return `$DeviceBuyerList`

**Final Result:** This process concludes by returning a [List] value.