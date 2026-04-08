# Microflow Analysis: ACT_DeviceAllocation_Finalize

### Requirements (Inputs):
- **$DAHelper** (A record of type: EcoATM_DA.DAHelper)

### Execution Steps:
1. **Close Form**
2. **Run another process: "Custom_Logging.SUB_Log_Info"**
3. **Retrieve
      - Store the result in a new variable called **$DAWeek****
4. **Update the **$undefined** (Object):
      - Change [EcoATM_DA.DAWeek.IsFinalized] to: "true
"
      - Change [EcoATM_DA.DAWeek.FinalizeTimeStamp] to: "[%CurrentDateTime%]
"
      - **Save:** This change will be saved to the database immediately.**
5. **Show Page**
6. **Run another process: "Custom_Logging.SUB_Log_Info"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
