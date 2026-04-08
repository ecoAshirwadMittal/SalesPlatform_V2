# Microflow Analysis: Val_ProcessTaskQueue

### Requirements (Inputs):
- **$TaskQueue** (A record of type: TaskQueueScheduler.TaskQueue)

### Execution Steps:
1. **Run another process: "TaskQueueScheduler.Val_TaskQueue_FullName_Required"
      - Store the result in a new variable called **$FullName_Required****
2. **Run another process: "TaskQueueScheduler.Val_TaskQueue_Name_Required"
      - Store the result in a new variable called **$Name_Required****
3. **Run another process: "TaskQueueScheduler.Val_TaskQueue_ShortName_Required"
      - Store the result in a new variable called **$ShortName_Required****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
