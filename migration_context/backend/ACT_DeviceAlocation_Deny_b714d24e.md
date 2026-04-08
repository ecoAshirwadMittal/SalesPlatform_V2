# Microflow Analysis: ACT_DeviceAlocation_Deny

### Requirements (Inputs):
- **$DeviceBuyer** (A record of type: EcoATM_DA.DeviceBuyer)
- **$NPE_ClearingBid** (A record of type: EcoATM_DA.NPE_ClearingBid)
- **$DeviceAllocation** (A record of type: EcoATM_DA.DeviceAllocation)
- **$DAHelper** (A record of type: EcoATM_DA.DAHelper)

### Execution Steps:
1. **Decision:** "Reject Reason Available?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Update the **$undefined** (Object):
      - Change [EcoATM_DA.DeviceBuyer.IsChanged] to: "true
"
      - Change [EcoATM_DA.DeviceBuyer.Reject] to: "true
"**
3. **Update the **$undefined** (Object):
      - Change [EcoATM_DA.DeviceAllocation.IsChanged] to: "true
"**
4. **Run another process: "EcoATM_DA.DeviceAllocation_Save"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
