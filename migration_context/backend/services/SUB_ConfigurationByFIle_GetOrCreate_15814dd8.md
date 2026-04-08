# Microflow Analysis: SUB_ConfigurationByFIle_GetOrCreate

### Requirements (Inputs):
- **$ResourceFilename** (A record of type: Object)

### Execution Steps:
1. **Search the Database for **Custom_Logging.ConfigurationByFile** using filter: { [FileName=$ResourceFilename]
 } (Call this list **$ConfigurationByFile**)**
2. **Decision:** "found?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
