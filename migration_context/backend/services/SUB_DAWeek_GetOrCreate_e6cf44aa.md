# Microflow Analysis: SUB_DAWeek_GetOrCreate

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$DAWeek****
2. **Decision:** "DAWeek exists?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
