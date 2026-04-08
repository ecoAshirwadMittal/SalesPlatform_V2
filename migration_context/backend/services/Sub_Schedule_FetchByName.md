# Microflow Detailed Specification: Sub_Schedule_FetchByName

### 📥 Inputs (Parameters)
- **$MicroflowName** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **TaskQueueScheduler.Schedule** Filter: `[MicroflowName=$MicroflowName or OldMicroflowName=$MicroflowName]` (Result: **$Schedule**)**
2. 🔀 **DECISION:** `$Schedule=empty`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$Schedule`
   ➔ **If [true]:**
      1. **JavaCallAction**
      2. 🔀 **DECISION:** `$MicroflowList=empty`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `empty`
         ➔ **If [false]:**
            1. **List Operation: **Find** on **$undefined** where `$MicroflowName` (Result: **$Microflow**)**
            2. 🔀 **DECISION:** `$Microflow!=empty`
               ➔ **If [true]:**
                  1. **Create **TaskQueueScheduler.Schedule** (Result: **$NewSchedule**)
      - Set **MicroflowName** = `$MicroflowName`**
                  2. 🏁 **END:** Return `$NewSchedule`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.