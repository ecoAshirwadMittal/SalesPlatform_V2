# Microflow Analysis: ACT_DeviceAllocation_ReviewAll

### Requirements (Inputs):
- **$DAHelper** (A record of type: EcoATM_DA.DAHelper)
- **$DeviceAllocationList** (A record of type: EcoATM_DA.DeviceAllocation)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Update the **$undefined** (Object):
      - Change [EcoATM_DA.DeviceAllocation_DAHelper] to: "$DeviceAllocationList
"**
3. **Run another process: "EcoATM_DA.ACT_DeviceAllocation_SeeBids"**
4. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
