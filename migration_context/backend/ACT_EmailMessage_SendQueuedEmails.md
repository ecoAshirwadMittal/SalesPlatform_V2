# Microflow Detailed Specification: ACT_EmailMessage_SendQueuedEmails

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔀 **DECISION:** `$EmailAccount/isOutgoingEmailConfigured`
   ➔ **If [true]:**
      1. **DB Retrieve **Email_Connector.EmailMessage** Filter: `[Email_Connector.EmailMessage_EmailAccount = $EmailAccount and QueuedForSending = true() and (Status = 'QUEUED' or Status = 'ERROR')]` (Result: **$EmailList**)**
      2. 🔄 **LOOP:** For each **$IteratorEmail** in **$EmailList**
         │ 1. **Call Microflow **Email_Connector.SUB_SendQueuedEmail** (Result: **$Variable**)**
         └─ **End Loop**
      3. **AggregateList**
      4. 🔀 **DECISION:** `$EmailsProcessed > 0`
         ➔ **If [true]:**
            1. **LogMessage**
            2. **Commit/Save **$EmailList** to Database**
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Commit/Save **$EmailList** to Database**
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Email_Connector.SUB_CreateLogItem****
      2. **Show Message (Error): `Selected email account needs to be configured with outgoing server details to queued emails.`**
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.