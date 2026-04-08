# Microflow Detailed Specification: ACT_DeviceBuyers_Delete_Admin

### 📥 Inputs (Parameters)
- **$DAWeek** (Type: EcoATM_DA.DAWeek)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Retrieve related **DeviceBuyer_DAWeek** via Association from **$DAWeek** (Result: **$DeviceBuyerList**)**
3. **Delete**
4. **Call Microflow **Custom_Logging.SUB_Log_Info****
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.