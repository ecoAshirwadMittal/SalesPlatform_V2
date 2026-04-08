# Microflow Analysis: ACT_Schedule_RunAsUser_Create

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Decision:** "Microflow Name?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Create Variable**
3. **Create Variable**
4. **Run another process: "TaskQueueScheduler.DS_RunAsUser"
      - Store the result in a new variable called **$User****
5. **Update the **$undefined** (Object):
      - Change [TaskQueueScheduler.Schedule.RunAsUser] to: "$User/Name"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
