# Microflow Analysis: DS_FetchDespocoConfiguration

### Execution Steps:
1. **Search the Database for **EcoATM_PWSIntegration.DeposcoConfig** using filter: { Show everything } (Call this list **$DeposcoConfig**)**
2. **Decision:** "DeposcoConfig exists?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
