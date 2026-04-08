# Microflow Analysis: Sub_QueuedTasks_SchedulesRunning

### Requirements (Inputs):
- **$QueuedTaskList** (A record of type: TaskQueueScheduler.QueuedTask)

### Execution Steps:
1. **Create List
      - Store the result in a new variable called **$UpdateList****
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Permanently save **$undefined** to the database.**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
