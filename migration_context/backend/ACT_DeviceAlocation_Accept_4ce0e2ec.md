# Microflow Analysis: ACT_DeviceAlocation_Accept

### Requirements (Inputs):
- **$DeviceBuyer** (A record of type: EcoATM_DA.DeviceBuyer)
- **$NPE_ClearingBid** (A record of type: EcoATM_DA.NPE_ClearingBid)
- **$DeviceAllocation** (A record of type: EcoATM_DA.DeviceAllocation)
- **$DAHelper** (A record of type: EcoATM_DA.DAHelper)

### Execution Steps:
1. **Decision:** "Reject Reason Available?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
2. **Validation Feedback**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
