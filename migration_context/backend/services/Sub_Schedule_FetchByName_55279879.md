# Microflow Analysis: Sub_Schedule_FetchByName

### Requirements (Inputs):
- **$MicroflowName** (A record of type: Object)

### Execution Steps:
1. **Search the Database for **TaskQueueScheduler.Schedule** using filter: { [MicroflowName=$MicroflowName or OldMicroflowName=$MicroflowName]
 } (Call this list **$Schedule**)**
2. **Decision:** "empty?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
