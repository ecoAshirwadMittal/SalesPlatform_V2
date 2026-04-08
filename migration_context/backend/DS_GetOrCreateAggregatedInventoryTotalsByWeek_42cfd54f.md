# Microflow Analysis: DS_GetOrCreateAggregatedInventoryTotalsByWeek

### Requirements (Inputs):
- **$SelectedWeek** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$AggreegatedInventoryTotal****
2. **Decision:** "Check condition"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
