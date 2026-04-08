# Microflow Analysis: DS_GetOrCreatePWSConstants

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.PWSConstants** using filter: { Show everything } (Call this list **$PWSConstants**)**
2. **Decision:** "exists?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
