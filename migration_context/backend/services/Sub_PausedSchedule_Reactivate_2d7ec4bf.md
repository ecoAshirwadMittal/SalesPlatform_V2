# Microflow Analysis: Sub_PausedSchedule_Reactivate

### Requirements (Inputs):
- **$PausedSchedule** (A record of type: TaskQueueScheduler.PausedSchedule)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$ScheduleList****
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Run another process: "TaskQueueScheduler.Sub_PreviousInstance_Refresh"**
4. **Delete**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
