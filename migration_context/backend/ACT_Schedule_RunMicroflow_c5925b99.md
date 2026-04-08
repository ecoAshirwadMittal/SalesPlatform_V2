# Microflow Analysis: ACT_Schedule_RunMicroflow

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Run another process: "TaskQueueScheduler.DS_MicroflowByName"
      - Store the result in a new variable called **$Microflow****
2. **Decision:** "Found?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Run another process: "TaskQueueScheduler.Sub_Schedule_ExecuteMicroflow"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
