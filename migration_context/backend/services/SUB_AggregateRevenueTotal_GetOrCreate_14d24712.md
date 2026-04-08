# Microflow Analysis: SUB_AggregateRevenueTotal_GetOrCreate

### Requirements (Inputs):
- **$DAWeek** (A record of type: EcoATM_DA.DAWeek)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$AggregateRevenueTotal****
2. **Decision:** "AgreedateRevenueTotal exists?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
