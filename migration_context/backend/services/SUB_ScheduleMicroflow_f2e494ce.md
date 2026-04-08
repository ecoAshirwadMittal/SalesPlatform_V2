# Microflow Analysis: SUB_ScheduleMicroflow

### Requirements (Inputs):
- **$TaskQueueName** (A record of type: Object)
- **$CompleteName** (A record of type: Object)
- **$NextRunTime** (A record of type: Object)
- **$IntervalType** (A record of type: Object)
- **$Interval** (A record of type: Object)
- **$Description** (A record of type: Object)

### Execution Steps:
1. **Run another process: "TaskQueueScheduler.DS_MicroflowByName"
      - Store the result in a new variable called **$Microflow****
2. **Decision:** "Found?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Search the Database for **TaskQueueScheduler.Schedule** using filter: { [MicroflowName = $Microflow/CompleteName] } (Call this list **$ExistingSchedule**)**
4. **Decision:** "Not found?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Search the Database for **TaskQueueScheduler.TaskQueue** using filter: { [FullName=$TaskQueueName
or Name=$TaskQueueName
or ShortName=$TaskQueueName
]
[AllowScheduling=true()]
 } (Call this list **$TaskQueue**)**
6. **Decision:** "Task Queue found?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Create Object
      - Store the result in a new variable called **$Schedule****
8. **Run another process: "TaskQueueScheduler.Val_ProcessSchedule"
      - Store the result in a new variable called **$Validations****
9. **Decision:** "Valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
10. **Permanently save **$undefined** to the database.**
11. **Log Message**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
