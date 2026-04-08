# Microflow Analysis: ACT_Carrier_DeleteByAdmin

### Requirements (Inputs):
- **$Carrier** (A record of type: EcoATM_PWSMDM.Carrier)

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$JSONContent****
2. **Run another process: "Custom_Logging.SUB_Log_Warning"**
3. **Delete**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
