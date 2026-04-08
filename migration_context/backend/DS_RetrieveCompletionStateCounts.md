# Microflow Detailed Specification: DS_RetrieveCompletionStateCounts

### 📥 Inputs (Parameters)
- **$Dummy** (Type: TaskQueueHelpers.ChartContext)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. **Create **TaskQueueHelpers.CompletionStateCount** (Result: **$CompletedCount**)
      - Set **CompletionStateName** = `'Completed'`
      - Set **Count** = `0`**
3. **Create **TaskQueueHelpers.CompletionStateCount** (Result: **$UncompletedCount**)
      - Set **CompletionStateName** = `'Uncompleted'`
      - Set **Count** = `0`**
4. 🔄 **LOOP:** For each **$IteratorProcessedQueueCount** in **$ReturnValueNameQueueCountList**
   │ 1. **Update **$CompletedCount**
      - Set **Count** = `$CompletedCount/Count + $IteratorProcessedQueueCount/CompletedCount`**
   │ 2. **Update **$UncompletedCount**
      - Set **Count** = `$UncompletedCount/Count + $IteratorProcessedQueueCount/UncompletedCount`**
   └─ **End Loop**
5. **CreateList**
6. **Add **$$CompletedCount** to/from list **$CompletionStateCountList****
7. **Add **$$UncompletedCount** to/from list **$CompletionStateCountList****
8. 🏁 **END:** Return `$CompletionStateCountList`

**Final Result:** This process concludes by returning a [List] value.