# Microflow Analysis: Sub_Schedule_ExecuteMicroflow

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Decision:** "Has Queue?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
2. **Update the **$undefined** (Object):
      - Change [TaskQueueScheduler.Schedule.Running] to: "true"
      - Change [TaskQueueScheduler.Schedule.LastRunTime] to: "[%CurrentDateTime%]"
      - Change [TaskQueueScheduler.Schedule.LastStarted] to: "empty"
      - Change [TaskQueueScheduler.Schedule.LastProcessed] to: "empty"
      - **Save:** This change will be saved to the database immediately.**
3. **Java Action Call
      - Store the result in a new variable called **$Queued**** ⚠️ *(This step has a safety catch if it fails)*
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
