# Microflow Detailed Specification: ACT_Schedule_UpdateAll

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Offset** = `0`**
2. **Create Variable **$BatchSize** = `0`**
3. 🔄 **LOOP:** For each **$undefined** in **$undefined**
   │ 1. **DB Retrieve **TaskQueueScheduler.Schedule**  (Result: **$ScheduleList**)**
   │ 2. **AggregateList**
   │ 3. 🔀 **DECISION:** `$Count>0`
   │    ➔ **If [false]:**
   │    ➔ **If [true]:**
   │       1. 🔄 **LOOP:** For each **$IteratorSchedule** in **$ScheduleList**
   │          │ 1. **Call Microflow **TaskQueueScheduler.Sub_UPDSchedule** (Result: **$UpdatedObject**)**
   │          └─ **End Loop**
   │       2. **Update Variable **$Offset** = `$Offset+$Count`**
   │       3. **Commit/Save **$ScheduleList** to Database**
   │       4. **Call Microflow **TaskQueueScheduler.SUB_Transaction_End_StartNew****
   │       5. **LogMessage**
   └─ **End Loop**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.