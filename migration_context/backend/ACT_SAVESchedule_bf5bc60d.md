# Microflow Analysis: ACT_SAVESchedule

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Run another process: "TaskQueueScheduler.Val_ProcessSchedule"
      - Store the result in a new variable called **$Validations****
2. **Decision:** "Valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Permanently save **$undefined** to the database.**
4. **Close Form**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
