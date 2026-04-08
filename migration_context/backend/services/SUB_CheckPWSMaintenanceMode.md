# Microflow Detailed Specification: SUB_CheckPWSMaintenanceMode

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.MaintenanceMode** Filter: `[IsEnabled and '[%CurrentDateTime%]' > StartTime and '[%CurrentDateTime%]' < EndTime]` (Result: **$MaintenanceMode**)**
2. 🏁 **END:** Return `$MaintenanceMode`

**Final Result:** This process concludes by returning a [Object] value.