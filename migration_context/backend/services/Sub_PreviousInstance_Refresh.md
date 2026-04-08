# Microflow Detailed Specification: Sub_PreviousInstance_Refresh

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **TaskQueueScheduler.PreviousInstance**  (Result: **$PreviousInstanceList_Delete**)**
2. **Delete**
3. **CreateList**
4. **DB Retrieve **System.XASInstance**  (Result: **$XASInstanceList**)**
5. 🔄 **LOOP:** For each **$IteratorXASInstance** in **$XASInstanceList**
   │ 1. **Create **TaskQueueScheduler.PreviousInstance** (Result: **$NewPreviousInstance**)
      - Set **XASId** = `$IteratorXASInstance/XASId`**
   │ 2. **Add **$$NewPreviousInstance** to/from list **$PreviousInstanceList****
   └─ **End Loop**
6. **Commit/Save **$PreviousInstanceList** to Database**
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.