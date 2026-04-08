# Microflow Analysis: ACT_PurgeIntegrationAudit

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
3. **Create Variable**
4. **Create Variable**
5. **Create Variable**
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
