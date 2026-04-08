# Microflow Detailed Specification: BCo_Schedule

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **TaskQueueScheduler.Sub_UPDSchedule** (Result: **$UpdatedObject**)**
2. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.