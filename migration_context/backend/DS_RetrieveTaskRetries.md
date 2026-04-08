# Microflow Detailed Specification: DS_RetrieveTaskRetries

### 📥 Inputs (Parameters)
- **$ProcessedQueueTask** (Type: System.ProcessedQueueTask)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **System.ProcessedQueueTask** Filter: `[Sequence = $ProcessedQueueTask/Sequence]` (Result: **$ProcessedQueueTaskRetryList**)**
2. 🏁 **END:** Return `$ProcessedQueueTaskRetryList`

**Final Result:** This process concludes by returning a [List] value.