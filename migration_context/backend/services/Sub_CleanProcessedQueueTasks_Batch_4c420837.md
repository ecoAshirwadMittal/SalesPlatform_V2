# Microflow Analysis: Sub_CleanProcessedQueueTasks_Batch

### Requirements (Inputs):
- **$QueuedAction** (A record of type: TaskQueueScheduler.QueuedAction)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$QueuedActionParameters****
2. **Create Variable**
3. **Create Variable**
4. **Search the Database for **System.ProcessedQueueTask** using filter: { [
(Status = 'Completed' and Started < $CompletionDateLimit)
or
(Status != 'Completed' and Started < $NonCompletionDateLimit)
]
 } (Call this list **$DeleteList**)**
5. **Delete**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
