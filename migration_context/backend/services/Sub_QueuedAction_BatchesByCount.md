# Microflow Detailed Specification: Sub_QueuedAction_BatchesByCount

### 📥 Inputs (Parameters)
- **$QueuedActionParameters** (Type: TaskQueueScheduler.QueuedActionParameters)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$BatchCount** = `0`**
2. **CreateList**
3. **CreateList**
4. **Create **TaskQueueScheduler.QueuedActionParameters** (Result: **$BatchQueuedActionParameters**)
      - Set **ProcessName** = `$QueuedActionParameters/ProcessName`
      - Set **BatchSize** = `$QueuedActionParameters/BatchSize`
      - Set **Offset** = `$BatchCount*$QueuedActionParameters/BatchSize`
      - Set **ReferenceText** = `$QueuedActionParameters/ReferenceText +' '+toString($BatchCount+1)`
      - Set **Param1** = `$QueuedActionParameters/Param1`
      - Set **Param2** = `$QueuedActionParameters/Param2`
      - Set **Param3** = `$QueuedActionParameters/Param3`**
5. **Call Microflow **TaskQueueScheduler.Sub_QueuedAction_Batch** (Result: **$BatchQueuedAction**)**
6. **Update **$BatchQueuedActionParameters**
      - Set **QueuedActionParameters_QueuedAction** = `$BatchQueuedAction`**
7. **Add **$$BatchQueuedAction** to/from list **$BatchQueuedActionList****
8. **Add **$$BatchQueuedActionParameters** to/from list **$BatchQueuedActionParametersList****
9. **Update Variable **$BatchCount** = `$BatchCount+1`**
10. 🔀 **DECISION:** `$BatchCount*$QueuedActionParameters/BatchSize >= $QueuedActionParameters/Count`
   ➔ **If [false]:**
         *(Merging with existing path logic)*
   ➔ **If [true]:**
      1. **Commit/Save **$BatchQueuedActionList** to Database**
      2. **Commit/Save **$BatchQueuedActionParametersList** to Database**
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.