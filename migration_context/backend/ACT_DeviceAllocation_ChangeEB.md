# Microflow Detailed Specification: ACT_DeviceAllocation_ChangeEB

### 📥 Inputs (Parameters)
- **$DeviceAllocation** (Type: EcoATM_DA.DeviceAllocation)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)
- **$DeviceBuyer** (Type: EcoATM_DA.DeviceBuyer)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Update **$DeviceAllocation** (and Save to DB)
      - Set **IsChanged** = `true`**
3. **Update **$DeviceBuyer** (and Save to DB)
      - Set **Bid** = `$DeviceAllocation/EB`
      - Set **IsChanged** = `true`**
4. **Commit/Save **$DeviceAllocation** to Database**
5. **Close current page/popup**
6. **Call Microflow **Custom_Logging.SUB_Log_Info****
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.