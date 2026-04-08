# Microflow Detailed Specification: ACT_Schedule_RunAsUser_Select

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)
- **$User** (Type: System.User)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$Schedule**
      - Set **RunAsUser** = `$User/Name`**
2. **Close current page/popup**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.