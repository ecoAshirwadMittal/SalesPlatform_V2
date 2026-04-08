# Microflow Detailed Specification: Sub_Schedule_UpdateAll_BatchPerObject

### 📥 Inputs (Parameters)
- **$QueuedAction** (Type: TaskQueueScheduler.QueuedAction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **QueuedActionParameters_QueuedAction** via Association from **$QueuedAction** (Result: **$QueuedActionParameters**)**
2. **JavaCallAction**
3. **Call Microflow **TaskQueueScheduler.Sub_UPDSchedule** (Result: **$UpdatedObject**)**
4. **Commit/Save **$Schedule** to Database**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.