# Microflow Analysis: Sub_QueuedAction_BatchesByCount

### Requirements (Inputs):
- **$QueuedActionParameters** (A record of type: TaskQueueScheduler.QueuedActionParameters)

### Execution Steps:
1. **Create Variable**
2. **Create List
      - Store the result in a new variable called **$BatchQueuedActionList****
3. **Create List
      - Store the result in a new variable called **$BatchQueuedActionParametersList****
4. **Create Object
      - Store the result in a new variable called **$BatchQueuedActionParameters****
5. **Run another process: "TaskQueueScheduler.Sub_QueuedAction_Batch"
      - Store the result in a new variable called **$BatchQueuedAction****
6. **Update the **$undefined** (Object):
      - Change [TaskQueueScheduler.QueuedActionParameters_QueuedAction] to: "$BatchQueuedAction"**
7. **Change List**
8. **Change List**
9. **Change Variable**
10. **Decision:** "Done?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**

**Conclusion:** This process sends back a [Void] result.
