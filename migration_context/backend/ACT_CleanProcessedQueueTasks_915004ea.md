# Microflow Analysis: ACT_CleanProcessedQueueTasks

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Search the Database for **System.ProcessedQueueTask** using filter: { [
(Status = 'Completed' and Started < $CompletionDateLimit)
or
(Status != 'Completed' and Started < $NonCompletionDateLimit)
] } (Call this list **$ProcessedQueueTaskList**)**
4. **Aggregate List
      - Store the result in a new variable called **$Count****
5. **Java Action Call
      - Store the result in a new variable called **$Sub_CleanProcessedQueueTasks_Batch****
6. **Run another process: "TaskQueueScheduler.Sub_QueuedAction_CreateBatches"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
