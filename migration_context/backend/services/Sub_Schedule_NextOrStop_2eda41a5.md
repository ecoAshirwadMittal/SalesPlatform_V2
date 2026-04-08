# Microflow Analysis: Sub_Schedule_NextOrStop

### Requirements (Inputs):
- **$TaskQueueName** (A record of type: Object)
- **$MicroflowName** (A record of type: Object)
- **$NextRunTime** (A record of type: Object)
- **$IntervalType** (A record of type: Object)
- **$Interval** (A record of type: Object)
- **$Description** (A record of type: Object)
- **$MaxBatches** (A record of type: Object)
- **$BatchCount** (A record of type: Object)

### Execution Steps:
1. **Decision:** "Next or Stop?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Search the Database for **TaskQueueScheduler.Schedule** using filter: { [MicroflowName=$MicroflowName] } (Call this list **$Schedule**)**
3. **Search the Database for **TaskQueueScheduler.TaskQueue** using filter: { [FullName=$TaskQueueName
or Name=$TaskQueueName
or ShortName=$TaskQueueName
]
[AllowScheduling=true()]
 } (Call this list **$TaskQueue**)**
4. **Decision:** "TaskQueue?"
   - If [true] -> Move to: **Schedule?**
   - If [false] -> Move to: **Finish**
5. **Decision:** "Schedule?"
   - If [false] -> Move to: **Next?**
   - If [true] -> Move to: **Next?**
6. **Decision:** "Next?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
