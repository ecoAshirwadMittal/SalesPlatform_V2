# Microflow Detailed Specification: ACT_ProcessQueueToSchedule

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **MicroflowScheduler.Schedule** Filter: `[MicroflowName=empty]` (Result: **$DeleteEmptyScheduleList**)**
2. **Delete**
3. **DB Retrieve **ProcessQueue.Process**  (Result: **$ProcessList**)**
4. **CreateList**
5. 🔄 **LOOP:** For each **$IteratorProcess** in **$ProcessList**
   │ 1. **Call Microflow **MicroflowScheduler.Sub_MapProcessQueueToSchedule** (Result: **$Schedule**)**
   │ 2. 🔀 **DECISION:** `$Schedule=empty`
   │    ➔ **If [true]:**
   │    ➔ **If [false]:**
   │       1. **Add **$$Schedule** to/from list **$ScheduleList****
   └─ **End Loop**
6. **Commit/Save **$ScheduleList** to Database**
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.