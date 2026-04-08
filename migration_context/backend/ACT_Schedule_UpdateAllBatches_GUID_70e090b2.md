# Microflow Analysis: ACT_Schedule_UpdateAllBatches_GUID

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Create Variable**
4. **Java Action Call
      - Store the result in a new variable called **$Sub_Schedule_UpdateAll_Batch_GUID****
5. **Run another process: "TaskQueueScheduler.Cal_QueuedAction_ReferenceText"
      - Store the result in a new variable called **$ReferenceText****
6. **Run another process: "TaskQueueScheduler.Sub_QueuedAction_BatchesByGUID"
      - Store the result in a new variable called **$BatchCount****
7. **Show Message**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
