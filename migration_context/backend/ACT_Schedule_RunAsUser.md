# Microflow Detailed Specification: ACT_Schedule_RunAsUser

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$RunAsUserRole** = `@TaskQueueScheduler.RunAsUserRole`**
2. **DB Retrieve **System.User** Filter: `[contains(Name,$Schedule/RunAsUser)] [System.UserRoles/System.UserRole/Name=$RunAsUserRole]` (Result: **$UserList**)**
3. **AggregateList**
4. 🔀 **DECISION:** `$Count=0`
   ➔ **If [true]:**
      1. **ValidationFeedback**
      2. **Update **$Schedule**
      - Set **RunAsUser** = `empty`**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$Count=1`
         ➔ **If [true]:**
            1. **List Operation: **Head** on **$undefined** (Result: **$User**)**
            2. **Update **$Schedule**
      - Set **RunAsUser** = `$User/Name`**
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Maps to Page: **TaskQueueScheduler.Schedule_RunAsUser_Select****
            2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.