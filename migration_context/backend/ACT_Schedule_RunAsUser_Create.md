# Microflow Detailed Specification: ACT_Schedule_RunAsUser_Create

### 📥 Inputs (Parameters)
- **$Schedule** (Type: TaskQueueScheduler.Schedule)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Schedule/MicroflowName!=empty and trim($Schedule/MicroflowName)!=''`
   ➔ **If [true]:**
      1. **Create Variable **$Pos** = `find($Schedule/MicroflowName,'.')+1`**
      2. **Create Variable **$UserName** = `if $Pos>0 then substring($Schedule/MicroflowName,$Pos,(length($Schedule/MicroflowName)-$Pos)) else $Schedule/MicroflowName`**
      3. **Call Microflow **TaskQueueScheduler.DS_RunAsUser** (Result: **$User**)**
      4. **Update **$Schedule**
      - Set **RunAsUser** = `$User/Name`**
      5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.