# Microflow Analysis: ACT_Schedule_RunAsUser

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Create Variable**
2. **Search the Database for **System.User** using filter: { [contains(Name,$Schedule/RunAsUser)]
[System.UserRoles/System.UserRole/Name=$RunAsUserRole] } (Call this list **$UserList**)**
3. **Aggregate List
      - Store the result in a new variable called **$Count****
4. **Decision:** "=0"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **=1**
5. **Validation Feedback**
6. **Update the **$undefined** (Object):
      - Change [TaskQueueScheduler.Schedule.RunAsUser] to: "empty"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
