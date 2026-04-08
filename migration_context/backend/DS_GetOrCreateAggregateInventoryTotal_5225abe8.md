# Microflow Analysis: DS_GetOrCreateAggregateInventoryTotal

### Requirements (Inputs):
- **$AgreegateInventoryHelper** (A record of type: EcoATM_Inventory.AggregateInventoryHelper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SelectedWeek****
2. **Retrieve
      - Store the result in a new variable called **$AggregatedInventoryWeek****
3. **Retrieve
      - Store the result in a new variable called **$TotalInventoryAgreegation****
4. **Decision:** "Check condition"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
