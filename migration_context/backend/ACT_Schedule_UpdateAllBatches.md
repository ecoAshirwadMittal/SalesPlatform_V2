# Microflow Detailed Specification: ACT_Schedule_UpdateAllBatches

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **TaskQueueScheduler.Schedule**  (Result: **$UpdateList**)**
2. **AggregateList**
3. **JavaCallAction**
4. **Create Variable **$BatchSize** = `5`**
5. **Create Variable **$ReferenceText** = `'Batch'`**
6. **Call Microflow **TaskQueueScheduler.Sub_QueuedAction_CreateBatches****
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.