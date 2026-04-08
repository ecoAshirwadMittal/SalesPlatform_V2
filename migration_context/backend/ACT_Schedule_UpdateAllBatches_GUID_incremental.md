# Microflow Detailed Specification: ACT_Schedule_UpdateAllBatches_GUID_incremental

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$DemoFlipper** = `'xxDEMOxx'`**
2. **Create Variable **$OQL** = `'select cast(id as LONG) as ObjectId from TaskQueueScheduler.Schedule where Description like ''%'+$DemoFlipper+''''`**
3. **Create Variable **$MaxBatches** = `2`**
4. **Create Variable **$BatchSize** = `3`**
5. **JavaCallAction**
6. **Call Microflow **TaskQueueScheduler.Cal_QueuedAction_ReferenceText** (Result: **$ReferenceText**)**
7. **Call Microflow **TaskQueueScheduler.Sub_QueuedAction_BatchesByGUID** (Result: **$BatchCount**)**
8. **JavaCallAction**
9. **Call Microflow **TaskQueueScheduler.Sub_Schedule_NextOrStop** (Result: **$Schedule**)**
10. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.