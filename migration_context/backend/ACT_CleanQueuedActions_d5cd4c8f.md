# Microflow Analysis: ACT_CleanQueuedActions

### Execution Steps:
1. **Search the Database for **TaskQueueScheduler.QueuedAction** using filter: { [not(TaskQueueScheduler.QueuedAction_Schedule/TaskQueueScheduler.Schedule)] } (Call this list **$QueuedActionList**)**
2. **Decision:** "QueuedActions?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Delete**
4. **Search the Database for **TaskQueueScheduler.QueuedActionParameters** using filter: { [not(TaskQueueScheduler.QueuedActionParameters_QueuedAction/TaskQueueScheduler.QueuedAction)] } (Call this list **$QueuedActionParametersList**)**
5. **Decision:** "QueuedAction Parameters?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
6. **Delete**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
