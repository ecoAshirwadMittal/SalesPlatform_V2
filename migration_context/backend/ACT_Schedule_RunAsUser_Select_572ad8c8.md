# Microflow Analysis: ACT_Schedule_RunAsUser_Select

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)
- **$User** (A record of type: System.User)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [TaskQueueScheduler.Schedule.RunAsUser] to: "$User/Name"**
2. **Close Form**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
