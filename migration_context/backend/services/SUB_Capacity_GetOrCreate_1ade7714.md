# Microflow Analysis: SUB_Capacity_GetOrCreate

### Requirements (Inputs):
- **$Capacity** (A record of type: Object)

### Execution Steps:
1. **Decision:** "exist?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
2. **Run another process: "Custom_Logging.SUB_Log_Warning"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
