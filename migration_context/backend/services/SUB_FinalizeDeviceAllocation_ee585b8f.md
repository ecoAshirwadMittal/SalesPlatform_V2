# Microflow Analysis: SUB_FinalizeDeviceAllocation

### Requirements (Inputs):
- **$DAHelper** (A record of type: EcoATM_DA.DAHelper)
- **$DAWeek** (A record of type: EcoATM_DA.DAWeek)

### Execution Steps:
1. **Decision:** "Is Not Finalized?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Show Page**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
