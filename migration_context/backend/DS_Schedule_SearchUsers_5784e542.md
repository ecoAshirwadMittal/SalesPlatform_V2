# Microflow Analysis: DS_Schedule_SearchUsers

### Requirements (Inputs):
- **$Schedule** (A record of type: TaskQueueScheduler.Schedule)

### Execution Steps:
1. **Create Variable**
2. **Search the Database for **System.User** using filter: { [contains(Name,$Schedule/RunAsUser)]
[System.UserRoles/System.UserRole/Name=$RunAsUserRole] } (Call this list **$UserList**)**
3. **Take the list **$UserList**, perform a [Sort], and call the result **$UserListSorted****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
