# Microflow Analysis: SUB_Note_GetOrCreate

### Requirements (Inputs):
- **$Note** (A record of type: Object)

### Execution Steps:
1. **Search the Database for **EcoATM_PWSMDM.Note** using filter: { [Notes=$Note]
 } (Call this list **$TargetNote**)**
2. **Decision:** "found?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
