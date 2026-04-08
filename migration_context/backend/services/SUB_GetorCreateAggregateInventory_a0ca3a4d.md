# Microflow Analysis: SUB_GetorCreateAggregateInventory

### Requirements (Inputs):
- **$Week1** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$AggregatedInventoryWeek****
2. **Decision:** "AggregatedInventoryWeek exists?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
