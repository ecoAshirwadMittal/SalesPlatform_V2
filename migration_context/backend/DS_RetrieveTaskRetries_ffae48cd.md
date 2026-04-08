# Microflow Analysis: DS_RetrieveTaskRetries

### Requirements (Inputs):
- **$ProcessedQueueTask** (A record of type: System.ProcessedQueueTask)

### Execution Steps:
1. **Search the Database for **System.ProcessedQueueTask** using filter: { [Sequence = $ProcessedQueueTask/Sequence] } (Call this list **$ProcessedQueueTaskRetryList**)**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
