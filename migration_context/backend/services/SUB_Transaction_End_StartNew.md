# Microflow Detailed Specification: SUB_Transaction_End_StartNew

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. 🔀 **DECISION:** `$IsInTransactionBeforeEnd`
   ➔ **If [true]:**
      1. **JavaCallAction**
      2. **JavaCallAction**
      3. 🔀 **DECISION:** `$IsInTransactionBeforeStart`
         ➔ **If [false]:**
            1. **JavaCallAction**
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Call Microflow **TaskQueueScheduler.SUB_Throw_Exception****
            2. **JavaCallAction**
            3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **TaskQueueScheduler.SUB_Throw_Exception****
      2. **JavaCallAction**
      3. **JavaCallAction**
      4. 🔀 **DECISION:** `$IsInTransactionBeforeStart`
         ➔ **If [false]:**
            1. **JavaCallAction**
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Call Microflow **TaskQueueScheduler.SUB_Throw_Exception****
            2. **JavaCallAction**
            3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.