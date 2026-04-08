# Microflow Analysis: SUB_DeleteAllDeviceAllocation_Admin

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Java Action Call
      - Store the result in a new variable called **$Variable****
4. **Search the Database for **EcoATM_DA.DeviceAllocation** using filter: { Show everything } (Call this list **$DeviceAllocationList**)**
5. **Aggregate List
      - Store the result in a new variable called **$Count****
6. **Delete**
7. **Decision:** "Check condition"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
8. **Show Message**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
