# Microflow Analysis: SUB_Condition_GetOrCreate

### Requirements (Inputs):
- **$Condition** (A record of type: Object)

### Execution Steps:
1. **Decision:** "exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Search the Database for **EcoATM_PWSMDM.Condition** using filter: { [Condition=$Condition]
 } (Call this list **$TargetCondition**)**
3. **Decision:** "found?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
