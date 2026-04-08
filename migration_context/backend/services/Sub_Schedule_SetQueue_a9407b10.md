# Microflow Analysis: Sub_Schedule_SetQueue

### Requirements (Inputs):
- **$MicroflowName** (A record of type: Object)
- **$QueueName** (A record of type: Object)

### Execution Steps:
1. **Search the Database for **TaskQueueScheduler.TaskQueue** using filter: { [FullName=$QueueName] } (Call this list **$TaskQueue**)**
2. **Decision:** "TaskQueue?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Run another process: "TaskQueueScheduler.Sub_Schedule_FetchByName"
      - Store the result in a new variable called **$Schedule****
4. **Update the **$undefined** (Object):
      - Change [TaskQueueScheduler.Schedule.QueueName] to: "$TaskQueue/FullName"
      - Change [TaskQueueScheduler.Schedule_TaskQueue] to: "$TaskQueue"
      - **Save:** This change will be saved to the database immediately.**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
