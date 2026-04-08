# Microflow Analysis: ACT_DeviceBuyers_Delete_Admin

### Requirements (Inputs):
- **$DAWeek** (A record of type: EcoATM_DA.DAWeek)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Retrieve
      - Store the result in a new variable called **$DeviceBuyerList****
3. **Delete**
4. **Run another process: "Custom_Logging.SUB_Log_Info"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
