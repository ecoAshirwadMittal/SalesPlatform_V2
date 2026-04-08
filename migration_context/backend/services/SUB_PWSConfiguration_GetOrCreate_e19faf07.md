# Microflow Analysis: SUB_PWSConfiguration_GetOrCreate

### Execution Steps:
1. **Search the Database for **EcoATM_PWSIntegration.PWSConfiguration** using filter: { Show everything } (Call this list **$PWSConfiguration**)**
2. **Decision:** "exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Run another process: "Custom_Logging.SUB_Log_Info"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
