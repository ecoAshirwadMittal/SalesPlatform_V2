# Microflow Analysis: Sub_QueuedAction_BatchesByGUID

### Requirements (Inputs):
- **$OQL** (A record of type: Object)
- **$MaxBatches** (A record of type: Object)
- **$BatchSize** (A record of type: Object)
- **$ReferenceText** (A record of type: Object)
- **$MicroflowName** (A record of type: Object)

### Execution Steps:
1. **Search the Database for **TaskQueueScheduler.Schedule** using filter: { [Running 
and MicroflowName=$MicroflowName
]
 } (Call this list **$Running**)**
2. **Decision:** "Not Running?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Integer] result.
