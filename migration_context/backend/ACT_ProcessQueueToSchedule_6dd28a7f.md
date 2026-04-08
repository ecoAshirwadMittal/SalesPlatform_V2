# Microflow Analysis: ACT_ProcessQueueToSchedule

### Execution Steps:
1. **Search the Database for **MicroflowScheduler.Schedule** using filter: { [MicroflowName=empty] } (Call this list **$DeleteEmptyScheduleList**)**
2. **Delete**
3. **Search the Database for **ProcessQueue.Process** using filter: { Show everything } (Call this list **$ProcessList**)**
4. **Create List
      - Store the result in a new variable called **$ScheduleList****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Permanently save **$undefined** to the database.**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
