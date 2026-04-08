# Microflow Analysis: ACT_Schedule_Microflow_Select

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)
- **$Microflow** (A record of type: TaskQueueScheduler.Microflow)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [TaskQueueScheduler.Schedule.MicroflowName] to: "$Microflow/CompleteName"
      - Change [TaskQueueScheduler.Schedule.OldMicroflowName] to: "$Microflow/CompleteName"**
2. **Close Form**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
