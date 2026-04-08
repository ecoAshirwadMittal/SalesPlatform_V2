# Microflow Analysis: ACT_Schedule_UpdateAllBatches_Sequential

### Execution Steps:
1. **Create Variable**
2. **Search the Database for **TaskQueueScheduler.Schedule** using filter: { [contains(MicroflowName,'Schedule_UpdateAll')]
[changedDate=empty or changedDate<$CurrentDateTime] } (Call this list **$UpdateList**)**
3. **Aggregate List
      - Store the result in a new variable called **$Count****
4. **Java Action Call
      - Store the result in a new variable called **$Sub_Schedule_UpdateAll_Batch_Sequential****
5. **Run another process: "TaskQueueScheduler.Sub_Schedule_SetQueue"**
6. **Create Variable**
7. **Create Variable**
8. **Run another process: "TaskQueueScheduler.Sub_QueuedAction_CreateBatches"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
