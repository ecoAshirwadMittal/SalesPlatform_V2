# Microflow Analysis: ACT_SAVETaskQueue

### Requirements (Inputs):
- **$TaskQueue** (A record of type: TaskQueueScheduler.TaskQueue)

### Execution Steps:
1. **Run another process: "TaskQueueScheduler.Val_ProcessTaskQueue"
      - Store the result in a new variable called **$Validations****
2. **Decision:** "Valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Permanently save **$undefined** to the database.**
4. **Run another process: "TaskQueueScheduler.Sub_TaskQueue_UpdateSchedules"**
5. **Close Form**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
