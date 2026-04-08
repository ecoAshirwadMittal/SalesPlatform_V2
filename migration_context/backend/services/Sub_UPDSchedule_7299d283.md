# Microflow Analysis: Sub_UPDSchedule

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$TaskQueue****
2. **Decision:** "empty?"
   - If [false] -> Move to: **QueueName changed?**
   - If [true] -> Move to: **Activity**
3. **Decision:** "QueueName changed?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Update the **$undefined** (Object):
      - Change [TaskQueueScheduler.Schedule.QueueName] to: "$TaskQueue/FullName
"**
5. **Decision:** "Microflow changed?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
6. **Update the **$undefined** (Object):
      - Change [TaskQueueScheduler.Schedule.OldMicroflowName] to: "$Schedule/MicroflowName"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
