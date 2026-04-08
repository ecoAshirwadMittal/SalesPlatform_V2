# Microflow Detailed Specification: ACT_Schedule_UpdateAllBatches_GUID

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$OQL** = `'select distinct cast(id as LONG) as ObjectId' +' from TaskQueueScheduler.Schedule'`**
2. **Create Variable **$MaxBatches** = `0`**
3. **Create Variable **$BatchSize** = `5`**
4. **JavaCallAction**
5. **Call Microflow **TaskQueueScheduler.Cal_QueuedAction_ReferenceText** (Result: **$ReferenceText**)**
6. **Call Microflow **TaskQueueScheduler.Sub_QueuedAction_BatchesByGUID** (Result: **$BatchCount**)**
7. **Show Message (Information): `Queued microflow {1} {2} {3}`**
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.