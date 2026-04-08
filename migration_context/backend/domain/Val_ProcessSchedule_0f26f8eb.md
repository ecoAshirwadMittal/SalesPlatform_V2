# Microflow Analysis: Val_ProcessSchedule

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Run another process: "TaskQueueScheduler.Val_Schedule_MicroflowName_Required"
      - Store the result in a new variable called **$Microflows_Required****
2. **Run another process: "TaskQueueScheduler.Val_Schedule_TaskQueue_Required"
      - Store the result in a new variable called **$TaskQueue_Required****
3. **Run another process: "TaskQueueScheduler.Val_Schedule_ActiveFrom_Required"
      - Store the result in a new variable called **$ActiveFrom_Required****
4. **Run another process: "TaskQueueScheduler.Val_Schedule_ActiveUntil_GreaterThan_ActiveFrom"
      - Store the result in a new variable called **$ActiveUntil_GreaterThan_ActiveFrom****
5. **Run another process: "TaskQueueScheduler.Val_Schedule_Interval_Required"
      - Store the result in a new variable called **$Interval_Required****
6. **Run another process: "TaskQueueScheduler.Val_Schedule_IntervalType_Required"
      - Store the result in a new variable called **$IntervalType_Required****
7. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
