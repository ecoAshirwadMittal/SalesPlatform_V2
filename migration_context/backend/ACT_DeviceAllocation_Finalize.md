# Microflow Detailed Specification: ACT_DeviceAllocation_Finalize

### 📥 Inputs (Parameters)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Close current page/popup**
2. **Call Microflow **Custom_Logging.SUB_Log_Info****
3. **Retrieve related **DAHelper_DAWeek** via Association from **$DAHelper** (Result: **$DAWeek**)**
4. **Update **$DAWeek** (and Save to DB)
      - Set **IsFinalized** = `true`
      - Set **FinalizeTimeStamp** = `[%CurrentDateTime%]`**
5. **Maps to Page: **EcoATM_DA.DeviceAllocation_FinalizeConfirm****
6. **Call Microflow **Custom_Logging.SUB_Log_Info****
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.