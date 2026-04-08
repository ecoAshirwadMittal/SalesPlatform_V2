# Microflow Analysis: ACT_CleanScheduledEventInformation

### Execution Steps:
1. **Create Variable**
2. **Search the Database for **System.ScheduledEventInformation** using filter: { [EndTime<$TimePeriod] } (Call this list **$ScheduledEventInformationList**)**
3. **Aggregate List
      - Store the result in a new variable called **$TotalScheduledEventInfrmationToDelete****
4. **Delete**
5. **Log Message**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
