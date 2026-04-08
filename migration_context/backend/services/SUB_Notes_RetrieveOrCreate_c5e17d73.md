# Microflow Analysis: SUB_Notes_RetrieveOrCreate

### Requirements (Inputs):
- **$Device** (A record of type: EcoATM_PWSMDM.Device)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$Note****
2. **Decision:** "Has note?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
