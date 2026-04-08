# Page: Schedule_RunAsUser_Select

**Layout:** `TaskQueueScheduler.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
    ↳ [Change] → **Microflow**: `TaskQueueScheduler.OCh_Schedule_SelectionRefresh`
  - 📦 **DataGrid** [MF: TaskQueueScheduler.DS_Schedule_SearchUsers]
      ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_Schedule_RunAsUser_Select`
    - 📊 **Column**: Username [Width: 100]
    ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_Schedule_SelectionCancel`
