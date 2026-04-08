# Microflow Detailed Specification: Sub_CleanProcessedQueueTasks_Batch

### 📥 Inputs (Parameters)
- **$QueuedAction** (Type: TaskQueueScheduler.QueuedAction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **QueuedActionParameters_QueuedAction** via Association from **$QueuedAction** (Result: **$QueuedActionParameters**)**
2. **Create Variable **$CompletionDateLimit** = `parseDateTimeUTC($QueuedActionParameters/Param1,'yyyy-MM-dd HH:mm:ss')`**
3. **Create Variable **$NonCompletionDateLimit** = `parseDateTimeUTC($QueuedActionParameters/Param2,'yyyy-MM-dd HH:mm:ss')`**
4. **DB Retrieve **System.ProcessedQueueTask** Filter: `[ (Status = 'Completed' and Started < $CompletionDateLimit) or (Status != 'Completed' and Started < $NonCompletionDateLimit) ]` (Result: **$DeleteList**)**
5. **Delete**
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.