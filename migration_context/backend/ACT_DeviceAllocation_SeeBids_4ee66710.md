# Microflow Analysis: ACT_DeviceAllocation_SeeBids

### Requirements (Inputs):
- **$DeviceAllocationList** (A record of type: EcoATM_DA.DeviceAllocation)
- **$DAHelper** (A record of type: EcoATM_DA.DAHelper)
- **$CurrentObjectDeviceAllocation** (A record of type: EcoATM_DA.DeviceAllocation)

### Execution Steps:
1. **Aggregate List
      - Store the result in a new variable called **$CountDeviceAllocationList**** ⚠️ *(This step has a safety catch if it fails)*
2. **Decision:** "Has List?"
   - If [true] -> Move to: **Below 50 Objects?**
   - If [false] -> Move to: **Not Single selection**
3. **Decision:** "Below 50 Objects?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Show Page**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
