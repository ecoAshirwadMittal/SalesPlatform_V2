# Microflow Detailed Specification: Sub_RunMicroflowOnQueue_ByName_Once

### 📥 Inputs (Parameters)
- **$MicroflowName** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **TaskQueueScheduler.Sub_Schedule_FetchByName** (Result: **$Schedule**)**
2. 🔀 **DECISION:** `$Schedule=empty`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$Schedule/Running`
         ➔ **If [false]:**
            1. **Call Microflow **TaskQueueScheduler.ACT_Schedule_RunMicroflow** (Result: **$Schedule_1**)**
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.