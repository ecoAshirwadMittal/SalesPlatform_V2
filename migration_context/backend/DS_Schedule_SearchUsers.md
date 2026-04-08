# Microflow Detailed Specification: DS_Schedule_SearchUsers

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$RunAsUserRole** = `@TaskQueueScheduler.RunAsUserRole`**
2. **DB Retrieve **System.User** Filter: `[contains(Name,$Schedule/RunAsUser)] [System.UserRoles/System.UserRole/Name=$RunAsUserRole]` (Result: **$UserList**)**
3. **List Operation: **Sort** on **$undefined** sorted by: Name (Ascending) (Result: **$UserListSorted**)**
4. 🏁 **END:** Return `$UserListSorted`

**Final Result:** This process concludes by returning a [List] value.