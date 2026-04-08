# Microflow Analysis: SUB_Color_GetOrCreate

### Requirements (Inputs):
- **$Color** (A record of type: Object)

### Execution Steps:
1. **Decision:** "exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Search the Database for **EcoATM_PWSMDM.Color** using filter: { [Color=$Color]
 } (Call this list **$TargetColor**)**
3. **Decision:** "found?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
