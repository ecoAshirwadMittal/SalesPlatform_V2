# Microflow Detailed Specification: ACT_Schedule_Microflow_Select

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)
- **$Microflow** (Type: TaskQueueScheduler.Microflow)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$Schedule**
      - Set **MicroflowName** = `$Microflow/CompleteName`
      - Set **OldMicroflowName** = `$Microflow/CompleteName`**
2. **Close current page/popup**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.