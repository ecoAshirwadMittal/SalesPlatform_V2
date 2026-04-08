# Page: Schedule_Overview

**Allowed Roles:** TaskQueueScheduler.Configurator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataView** [MF: TaskQueueScheduler.DS_PausedSchedule_Get]
- 📑 **TabContainer** [Class: `mx-tabcontainer--blue mt-4 mb-4`]
  - 📑 **Tab**: "Scheduled"
    - 📦 **DataGrid** [Context]
        ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_InactivateAllSchedules`
        ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_ActivateAllSchedules`
        ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_ADDSchedule`
      - ⚡ **Button**: Edit [Style: Default]
        ↳ [acti] → **Page**: `TaskQueueScheduler.Schedule_NewEdit`
      - ⚡ **Button**: Delete [Style: Default]
        ↳ [acti] → **Delete**
      - ⚡ **Button**: Run [Style: Default]
        ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_Schedule_RunMicroflow`
      - ⚡ **Button**: Export to Excel [Style: Default] [Class: `btn-lightblue`]
      - 📊 **Column**: Active [Width: 5]
      - 📊 **Column**: Running [Width: 5]
      - 📊 **Column**: Schedule queue [Width: 4]
      - 📊 **Column**: Microflow [Width: 19]
      - 📊 **Column**: Interval type [Width: 5]
      - 📊 **Column**: Interval [Width: 5]
      - 📊 **Column**: Last run time / Created [Width: 11]
      - 📊 **Column**: Last duration (milliseconds) [Width: 7]
      - 📊 **Column**: Next run day [Width: 11]
      - 📊 **Column**: Next run time [Width: 10]
      - 📊 **Column**: Active from [Width: 11]
      - 📊 **Column**: Active until [Width: 7]
  - 📑 **Tab**: "Queued/Running"
    - 📦 **DataGrid** [Context]
        ↳ [acti] → **Page**: `TaskQueueScheduler.Schedule_View`
        ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_Schedule_StopRunning`
      - 📊 **Column**: Running [Width: 5]
      - 📊 **Column**: Queue name [Width: 22]
      - 📊 **Column**: Microflow [Width: 22]
      - 📊 **Column**: Description [Width: 25]
      - 📊 **Column**: Running queued actions [Width: 7]
      - 📊 **Column**: Last run time / Created [Width: 11]
      - 📊 **Column**: Last duration (milliseconds) [Width: 8]
  - 📑 **Tab**: "Finished"
    - 📦 **DataGrid** [Context]
        ↳ [acti] → **Page**: `TaskQueueScheduler.ProcessedQueueTask_View`
        ↳ [acti] → **Delete**
      - 📊 **Column**: Sequence [Width: 4]
      - 📊 **Column**: Queue [Width: 20]
      - 📊 **Column**: Microflow [Width: 27]
      - 📊 **Column**: Status [Width: 8]
      - 📊 **Column**: Last run time / Created [Width: 11]
      - 📊 **Column**: Started [Width: 11]
      - 📊 **Column**: Finished [Width: 11]
      - 📊 **Column**: Duration (milliseconds) [Width: 8]
  - 📑 **Tab**: "Cancelled"
    - 📦 **DataGrid** [Context]
        ↳ [acti] → **Page**: `TaskQueueScheduler.ProcessedQueueTask_View`
        ↳ [acti] → **Delete**
      - 📊 **Column**: Queue [Width: 20]
      - 📊 **Column**: Microflow [Width: 27]
      - 📊 **Column**: Status [Width: 9]
      - 📊 **Column**: Last run time / Created [Width: 11]
      - 📊 **Column**: Started [Width: 11]
      - 📊 **Column**: Error message [Width: 22]
  - 📑 **Tab**: "Configuration"
    - 📂 **GroupBox**: "Samples"
        ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_Schedule_UpdateAllPerObject`
        ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_Schedule_UpdateAllBatches`
        ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_Schedule_UpdateAllBatches_GUID`
        ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_Schedule_UpdateAllBatches_GUID_incremental`
        ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_Schedule_UpdateAll_RunInBatch`
        ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_Schedule_UpdateAllBatches_Sequential`
    - 📦 **DataGrid** [Context]
        ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_ADDSchedule_Configuration`
      - ⚡ **Button**: Edit [Style: Default]
        ↳ [acti] → **Page**: `TaskQueueScheduler.Schedule_NewEdit_Configuration`
        ↳ [acti] → **Delete**
      - ⚡ **Button**: Run [Style: Default]
        ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_Schedule_RunMicroflow`
      - ⚡ **Button**: Export to Excel [Style: Default] [Class: `btn-lightblue`]
        ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_SHOWTaskQueuesOverview`
        ↳ [acti] → **Page**: `TaskQueueScheduler.QueuedAction_Overview`
      - 📊 **Column**: Queue name [Width: 21]
      - 📊 **Column**: Microflow [Width: 27]
      - 📊 **Column**: Description [Width: 33]
      - 📊 **Column**: Last run time / Created [Width: 11]
      - 📊 **Column**: Last duration (milliseconds) [Width: 8]
  - 📑 **Tab**: "Scheduled Events"
    - 📦 **DataGrid** [Context]
        ↳ [acti] → **Page**: `TaskQueueScheduler.ProcessedQueueTask_View`
        ↳ [acti] → **Delete**
      - 📊 **Column**: Sequence [Width: 6]
      - 📊 **Column**: Scheduled event [Width: 22]
      - 📊 **Column**: Microflow [Width: 22]
      - 📊 **Column**: Status [Width: 9]
      - 📊 **Column**: Last run time / Created [Width: 11]
      - 📊 **Column**: Started [Width: 11]
      - 📊 **Column**: Finished [Width: 11]
      - 📊 **Column**: Duration (milliseconds) [Width: 8]
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
