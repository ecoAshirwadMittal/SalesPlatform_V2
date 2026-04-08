# Microflow Detailed Specification: ACT_EmailMessageList_ResetQueuedStatus

### 📥 Inputs (Parameters)
- **$EmailList** (Type: Email_Connector.EmailMessage)

### ⚙️ Execution Flow (Logic Steps)
1. 🔄 **LOOP:** For each **$IteratorEmail** in **$EmailList**
   │ 1. **Call Microflow **Email_Connector.ACT_EmailMessage_ResetQueuedStatus****
   └─ **End Loop**
2. **Commit/Save **$EmailList** to Database**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.