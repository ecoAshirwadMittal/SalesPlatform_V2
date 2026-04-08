# Microflow Detailed Specification: ACT_Schedule_UpdateAllBatches_Sequential

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$CurrentDateTime** = `[%CurrentDateTime%]`**
2. **DB Retrieve **TaskQueueScheduler.Schedule** Filter: `[contains(MicroflowName,'Schedule_UpdateAll')] [changedDate=empty or changedDate<$CurrentDateTime]` (Result: **$UpdateList**)**
3. **AggregateList**
4. **JavaCallAction**
5. **Call Microflow **TaskQueueScheduler.Sub_Schedule_SetQueue****
6. **Create Variable **$BatchSize** = `3`**
7. **Create Variable **$ReferenceText** = `'Batch'`**
8. **Call Microflow **TaskQueueScheduler.Sub_QueuedAction_CreateBatches****
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.