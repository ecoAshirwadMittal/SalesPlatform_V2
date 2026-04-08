# Microflow Analysis: ACT_DeviceAllocation_ChangeEB

### Requirements (Inputs):
- **$DeviceAllocation** (A record of type: EcoATM_DA.DeviceAllocation)
- **$DAHelper** (A record of type: EcoATM_DA.DAHelper)
- **$DeviceBuyer** (A record of type: EcoATM_DA.DeviceBuyer)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Update the **$undefined** (Object):
      - Change [EcoATM_DA.DeviceAllocation.IsChanged] to: "true
"
      - **Save:** This change will be saved to the database immediately.**
3. **Update the **$undefined** (Object):
      - Change [EcoATM_DA.DeviceBuyer.Bid] to: "$DeviceAllocation/EB
"
      - Change [EcoATM_DA.DeviceBuyer.IsChanged] to: "true
"
      - **Save:** This change will be saved to the database immediately.**
4. **Permanently save **$undefined** to the database.**
5. **Close Form**
6. **Run another process: "Custom_Logging.SUB_Log_Info"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
