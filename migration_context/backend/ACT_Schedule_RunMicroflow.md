# Microflow Detailed Specification: ACT_Schedule_RunMicroflow

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **TaskQueueScheduler.DS_MicroflowByName** (Result: **$Microflow**)**
2. 🔀 **DECISION:** `$Microflow!=empty`
   ➔ **If [true]:**
      1. **Call Microflow **TaskQueueScheduler.Sub_Schedule_ExecuteMicroflow****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Information): `You cannot run this microflow here because it needs InputParameter(s)`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.