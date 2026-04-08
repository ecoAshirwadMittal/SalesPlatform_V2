# Microflow Analysis: Sub_Schedule_GetMicroflowName

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Create Variable**
2. **Retrieve
      - Store the result in a new variable called **$QueuedActionList****
3. **Decision:** "Found?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Take the list **$QueuedActionList**, perform a [Head], and call the result **$QueuedAction****
5. **Retrieve
      - Store the result in a new variable called **$QueuedActionParameters****
6. **Change Variable**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
