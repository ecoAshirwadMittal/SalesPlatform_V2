# Microflow Analysis: BCo_Schedule

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Run another process: "TaskQueueScheduler.Sub_UPDSchedule"
      - Store the result in a new variable called **$UpdatedObject****
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
