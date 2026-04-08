# Microflow Analysis: ACT_DeviceAllocation_Delete_Admin

### Requirements (Inputs):
- **$DAWeek** (A record of type: EcoATM_DA.DAWeek)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Search the Database for **EcoATM_DA.DeviceAllocation** using filter: { [EcoATM_DA.DeviceAllocation_DAWeek = $DAWeek]
 } (Call this list **$DeviceAllocationList**)**
3. **Decision:** "Check condition"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Delete**

**Conclusion:** This process sends back a [Void] result.
