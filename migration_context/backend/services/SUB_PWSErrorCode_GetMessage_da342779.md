# Microflow Analysis: SUB_PWSErrorCode_GetMessage

### Requirements (Inputs):
- **$ErrorCode** (A record of type: Object)
- **$SourceSystem** (A record of type: Object)

### Execution Steps:
1. **Search the Database for **EcoATM_PWSIntegration.PWSResponseConfig** using filter: { [SourceSystem=$SourceSystem]
[SourceErrorCode=$ErrorCode]
 } (Call this list **$PWSResponseConfig**)**
2. **Decision:** "found?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
