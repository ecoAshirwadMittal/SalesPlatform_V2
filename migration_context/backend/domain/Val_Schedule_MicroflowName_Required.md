# Microflow Detailed Specification: Val_Schedule_MicroflowName_Required

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Message** = `' is mandatory'`**
2. 🔀 **DECISION:** `$Schedule/MicroflowName!=empty and length(trim($Schedule/MicroflowName))>0`
   ➔ **If [false]:**
      1. **Create Variable **$Validation** = `'Microflow' + $Message`**
      2. **ValidationFeedback**
      3. 🏁 **END:** Return `$Validation`
   ➔ **If [true]:**
      1. **DB Retrieve **TaskQueueScheduler.Schedule** Filter: `[MicroflowName=$Schedule/MicroflowName]` (Result: **$ScheduleList**)**
      2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject!=$Schedule` (Result: **$ScheduleExists**)**
      3. 🔀 **DECISION:** `$ScheduleExists!=empty`
         ➔ **If [false]:**
            1. **List Operation: **Head** on **$undefined** (Result: **$ScheduleBeforeChanges**)**
            2. 🔀 **DECISION:** `$ScheduleBeforeChanges=empty or $Schedule/MicroflowName!=$ScheduleBeforeChanges/MicroflowName`
               ➔ **If [true]:**
                  1. **JavaCallAction**
                  2. 🔀 **DECISION:** `$MicroflowList!=empty`
                     ➔ **If [true]:**
                        1. **List Operation: **Find** on **$undefined** where `$Schedule/MicroflowName` (Result: **$Microflow**)**
                        2. 🔀 **DECISION:** `$Microflow!=empty`
                           ➔ **If [false]:**
                              1. **Update Variable **$Message** = `' is unknown'`**
                              2. **Create Variable **$Validation** = `'Microflow' + $Message`**
                              3. **ValidationFeedback**
                              4. 🏁 **END:** Return `$Validation`
                           ➔ **If [true]:**
                              1. 🏁 **END:** Return `''`
                     ➔ **If [false]:**
                        1. **Update Variable **$Message** = `' is unknown'`**
                        2. **Create Variable **$Validation** = `'Microflow' + $Message`**
                        3. **ValidationFeedback**
                        4. 🏁 **END:** Return `$Validation`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `''`
         ➔ **If [true]:**
            1. **Update Variable **$Message** = `' already exists as Schedule'`**
            2. **Create Variable **$Validation** = `'Microflow' + $Message`**
            3. **ValidationFeedback**
            4. 🏁 **END:** Return `$Validation`

**Final Result:** This process concludes by returning a [String] value.