# Microflow Analysis: ACT_Schedule_UpdateAllPerObject

### Execution Steps:
1. **Search the Database for **TaskQueueScheduler.Schedule** using filter: { Show everything } (Call this list **$UpdateList**)**
2. **Java Action Call
      - Store the result in a new variable called **$Sub_Schedule_UpdateAll_Batch****
3. **Create Variable**
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
