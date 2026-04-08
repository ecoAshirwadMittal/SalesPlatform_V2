# Microflow Analysis: Sub_QueuedAction_CreateBatch

### Requirements (Inputs):
- **$MicroflowName** (A record of type: Object)
- **$ReferenceText** (A record of type: Object)
- **$Param1** (A record of type: Object)
- **$Param2** (A record of type: Object)
- **$Param3** (A record of type: Object)
- **$ShowResult** (A record of type: Object)

### Execution Steps:
1. **Create Object
      - Store the result in a new variable called **$QueuedActionParameters****
2. **Run another process: "TaskQueueScheduler.Sub_QueuedAction_Batch"
      - Store the result in a new variable called **$QueuedAction****
3. **Decision:** "Show Result?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Run another process: "TaskQueueScheduler.Sub_QueuedAction_ShowResult"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
