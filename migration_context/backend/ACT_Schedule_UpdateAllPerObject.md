# Microflow Detailed Specification: ACT_Schedule_UpdateAllPerObject

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **TaskQueueScheduler.Schedule**  (Result: **$UpdateList**)**
2. **JavaCallAction**
3. **Create Variable **$ReferenceText** = `'Batch'`**
4. 🔄 **LOOP:** For each **$IteratorSchedule** in **$UpdateList**
   │ 1. **JavaCallAction**
   │ 2. **Call Microflow **TaskQueueScheduler.Sub_QueuedAction_CreateBatch****
   └─ **End Loop**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.