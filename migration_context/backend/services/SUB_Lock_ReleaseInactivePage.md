# Microflow Detailed Specification: SUB_Lock_ReleaseInactivePage

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Create Variable **$maxInactivityTime** = `addSeconds([%CurrentDateTime%], -@EcoATM_Lock.CONST_LOCK_RELEASE_AFTER)`**
3. **DB Retrieve **EcoATM_Lock.Lock** Filter: `[Active] [(LockUpdatedOn!=empty and LockUpdatedOn< $maxInactivityTime) or (LockUpdatedOn=empty and LockedOn < $maxInactivityTime)]` (Result: **$LockList**)**
4. 🔀 **DECISION:** `$LockList!=empty`
   ➔ **If [true]:**
      1. 🔄 **LOOP:** For each **$IteratorLock** in **$LockList**
         │ 1. **Update **$IteratorLock**
      - Set **Active** = `false`**
         └─ **End Loop**
      2. **Commit/Save **$LockList** to Database**
      3. **Call Microflow **Custom_Logging.SUB_Log_Info****
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.