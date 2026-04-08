# Microflow Analysis: ACT_Schedule_UpdateAllBatches

### Execution Steps:
1. **Search the Database for **TaskQueueScheduler.Schedule** using filter: { Show everything } (Call this list **$UpdateList**)**
2. **Aggregate List
      - Store the result in a new variable called **$Count****
3. **Java Action Call
      - Store the result in a new variable called **$Sub_Schedule_UpdateAll_Batch****
4. **Create Variable**
5. **Create Variable**
6. **Run another process: "TaskQueueScheduler.Sub_QueuedAction_CreateBatches"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
