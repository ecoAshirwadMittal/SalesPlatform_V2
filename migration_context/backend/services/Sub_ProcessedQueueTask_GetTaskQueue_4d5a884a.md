# Microflow Analysis: Sub_ProcessedQueueTask_GetTaskQueue

### Requirements (Inputs):
- **$ProcessedQueueTask** (A record of type: System.ProcessedQueueTask)

### Execution Steps:
1. **Search the Database for **TaskQueueScheduler.TaskQueue** using filter: { [FullName=$ProcessedQueueTask/QueueName] } (Call this list **$TaskQueue**)**
2. **Decision:** "Exists?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
