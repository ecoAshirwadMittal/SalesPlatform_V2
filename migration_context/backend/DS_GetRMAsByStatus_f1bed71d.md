# Microflow Analysis: DS_GetRMAsByStatus

### Requirements (Inputs):
- **$RMAStatusList** (A record of type: EcoATM_RMA.RMAStatus)

### Execution Steps:
1. **Decision:** "RMA Status Available?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Create List
      - Store the result in a new variable called **$RMAList****
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
