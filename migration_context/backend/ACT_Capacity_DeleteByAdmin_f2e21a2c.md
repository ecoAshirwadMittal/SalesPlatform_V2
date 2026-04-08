# Microflow Analysis: ACT_Capacity_DeleteByAdmin

### Requirements (Inputs):
- **$Capacity** (A record of type: EcoATM_PWSMDM.Capacity)

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$JSONContent****
2. **Run another process: "Custom_Logging.SUB_Log_Warning"**
3. **Delete**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
