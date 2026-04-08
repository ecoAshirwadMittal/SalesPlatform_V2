# Microflow Detailed Specification: ACT_Schedule_UpdateAll_RunInBatch

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. **Call Microflow **TaskQueueScheduler.Sub_RunMicroflowOnQueue_ByName****
3. **Show Message (Information): `{1} started on Batch queue`**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.