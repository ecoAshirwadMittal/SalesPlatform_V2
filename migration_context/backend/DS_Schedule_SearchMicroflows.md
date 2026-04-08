# Microflow Detailed Specification: DS_Schedule_SearchMicroflows

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. **DB Retrieve **TaskQueueScheduler.Schedule**  (Result: **$ScheduleList**)**
3. 🔄 **LOOP:** For each **$IteratorMicroflow** in **$MicroflowList**
   │ 1. **List Operation: **Find** on **$undefined** where `$IteratorMicroflow/CompleteName` (Result: **$ScheduleFound**)**
   │ 2. 🔀 **DECISION:** `$ScheduleFound!=empty`
   │    ➔ **If [false]:**
   │    ➔ **If [true]:**
   │       1. **List Operation: **Find** on **$undefined** where `$ScheduleFound/MicroflowName` (Result: **$MicroflowFound**)**
   │       2. **Remove **$$MicroflowFound** to/from list **$MicroflowList****
   └─ **End Loop**
4. **List Operation: **Sort** on **$undefined** sorted by: Name (Ascending) (Result: **$MicroflowListSorted**)**
5. 🏁 **END:** Return `$MicroflowListSorted`

**Final Result:** This process concludes by returning a [List] value.