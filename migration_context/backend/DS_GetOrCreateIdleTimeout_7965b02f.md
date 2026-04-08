# Microflow Analysis: DS_GetOrCreateIdleTimeout

### Execution Steps:
1. **Search the Database for **EcoATM_Direct_Theme.IdleTimeout** using filter: { [EcoATM_Direct_Theme.IdleTimeout_Session = $currentSession] } (Call this list **$IdleTimeout**)**
2. **Decision:** "Exists?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
