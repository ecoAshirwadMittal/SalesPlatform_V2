# Microflow Analysis: Sub_TaskQueue_CreateAll

### Execution Steps:
1. **Search the Database for **TaskQueueScheduler.TaskQueue** using filter: { Show everything } (Call this list **$TaskQueueList**)**
2. **Decision:** "empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Create Object
      - Store the result in a new variable called **$ScheduleQueue_1****
4. **Create Object
      - Store the result in a new variable called **$ScheduleQueue_2****
5. **Create Object
      - Store the result in a new variable called **$ScheduleQueue_3****
6. **Create Object
      - Store the result in a new variable called **$Batch****
7. **Create Object
      - Store the result in a new variable called **$Batch3Threads****
8. **Create Object
      - Store the result in a new variable called **$Batch1Clusterwide****
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
