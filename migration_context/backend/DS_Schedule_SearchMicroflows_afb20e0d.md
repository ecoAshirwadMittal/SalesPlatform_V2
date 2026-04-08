# Microflow Analysis: DS_Schedule_SearchMicroflows

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$MicroflowList****
2. **Search the Database for **TaskQueueScheduler.Schedule** using filter: { Show everything } (Call this list **$ScheduleList**)**
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Take the list **$MicroflowList**, perform a [Sort], and call the result **$MicroflowListSorted****
5. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
