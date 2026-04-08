# Microflow Analysis: DS_RetrieveCompletionStateCounts

### Requirements (Inputs):
- **$Dummy** (A record of type: TaskQueueHelpers.ChartContext)

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$ReturnValueNameQueueCountList****
2. **Create Object
      - Store the result in a new variable called **$CompletedCount****
3. **Create Object
      - Store the result in a new variable called **$UncompletedCount****
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Create List
      - Store the result in a new variable called **$CompletionStateCountList****
6. **Change List**
7. **Change List**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
