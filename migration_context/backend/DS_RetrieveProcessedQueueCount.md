# Microflow Detailed Specification: DS_RetrieveProcessedQueueCount

### 📥 Inputs (Parameters)
- **$Dummy** (Type: TaskQueueHelpers.ChartContext)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. 🔄 **LOOP:** For each **$IteratorProcessedQueueCount** in **$ReturnValueNameQueueCountList**
   │ 1. **Update **$IteratorProcessedQueueCount**
      - Set **TotalCount** = `$IteratorProcessedQueueCount/CompletedCount + $IteratorProcessedQueueCount/UncompletedCount`**
   └─ **End Loop**
3. 🏁 **END:** Return `$ReturnValueNameQueueCountList`

**Final Result:** This process concludes by returning a [List] value.