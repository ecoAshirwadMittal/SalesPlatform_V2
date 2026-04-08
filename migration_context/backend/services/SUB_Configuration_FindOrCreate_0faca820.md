# Microflow Analysis: SUB_Configuration_FindOrCreate

### Execution Steps:
1. **Search the Database for **DocumentGeneration.Configuration** using filter: { Show everything } (Call this list **$Configuration**)**
2. **Decision:** "Exists?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
