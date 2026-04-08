# Microflow Analysis: Sub_Schedule_ExecuteMicroflowWithParam

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)
- **$QueuedAction** (A record of type: TaskQueueScheduler.QueuedAction)

### Execution Steps:
1. **Decision:** "Has Queue?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
2. **Decision:** "Running already?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Update the **$undefined** (Object):
      - Change [TaskQueueScheduler.Schedule.RunningQueuedActions] to: "$Schedule/RunningQueuedActions+1"
      - **Save:** This change will be saved to the database immediately.**
4. **Java Action Call
      - Store the result in a new variable called **$QueuedAsUser**** ⚠️ *(This step has a safety catch if it fails)*
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
