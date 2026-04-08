# Microflow Detailed Specification: Sub_Schedule_UpdateAll_Batch_GUID

### 📥 Inputs (Parameters)
- **$QueuedAction** (Type: TaskQueueScheduler.QueuedAction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **QueuedActionParameters_QueuedAction** via Association from **$QueuedAction** (Result: **$QueuedActionParameters**)**
2. **JavaCallAction**
3. **CreateList**
4. **Create Variable **$DemoFlipper** = `'xxDEMOxx'`**
5. 🔄 **LOOP:** For each **$Iterator** in **$BatchObjectList**
   │ 1. **JavaCallAction**
   │ 2. **Update **$Schedule**
      - Set **Description** = `if $Schedule/Description=empty or $Schedule/Description='' then $DemoFlipper else if contains($Schedule/Description,$DemoFlipper) then replaceAll($Schedule/Description,$DemoFlipper,'') else $Schedule/Description+$DemoFlipper`**
   │ 3. **Add **$$Schedule** to/from list **$ScheduleList****
   └─ **End Loop**
6. **Commit/Save **$ScheduleList** to Database**
7. **Delete**
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.