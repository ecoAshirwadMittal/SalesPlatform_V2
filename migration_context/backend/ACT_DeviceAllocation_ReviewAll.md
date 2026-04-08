# Microflow Detailed Specification: ACT_DeviceAllocation_ReviewAll

### 📥 Inputs (Parameters)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)
- **$DeviceAllocationList** (Type: EcoATM_DA.DeviceAllocation)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Update **$DAHelper**
      - Set **DeviceAllocation_DAHelper** = `$DeviceAllocationList`**
3. **Call Microflow **EcoATM_DA.ACT_DeviceAllocation_SeeBids****
4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.