# Microflow Analysis: Sub_OQL_GetGUID

### Requirements (Inputs):
- **$OQL** (A record of type: Object)
- **$MaxBatches** (A record of type: Object)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "MaxBatches?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Create Variable**
4. **Java Action Call
      - Store the result in a new variable called **$ListBatchObject_Min****
5. **Take the list **$ListBatchObject_Min**, perform a [Head], and call the result **$BatchObject_Min****
6. **Change Variable**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Integer] result.
