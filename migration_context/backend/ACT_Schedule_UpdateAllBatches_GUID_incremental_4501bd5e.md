# Microflow Analysis: ACT_Schedule_UpdateAllBatches_GUID_incremental

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Create Variable**
4. **Create Variable**
5. **Java Action Call
      - Store the result in a new variable called **$Sub_Schedule_UpdateAll_Batch_GUID****
6. **Run another process: "TaskQueueScheduler.Cal_QueuedAction_ReferenceText"
      - Store the result in a new variable called **$ReferenceText****
7. **Run another process: "TaskQueueScheduler.Sub_QueuedAction_BatchesByGUID"
      - Store the result in a new variable called **$BatchCount****
8. **Java Action Call
      - Store the result in a new variable called **$ACT_Schedule_UpdateAllBatches_GUID_incremental****
9. **Run another process: "TaskQueueScheduler.Sub_Schedule_NextOrStop"
      - Store the result in a new variable called **$Schedule****
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
