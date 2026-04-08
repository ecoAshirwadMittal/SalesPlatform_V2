# Page: Schedule_Microflow_Select

**Layout:** `TaskQueueScheduler.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
    ↳ [Change] → **Microflow**: `TaskQueueScheduler.OCh_Schedule_SelectionRefresh`
  - 📦 **DataGrid** [MF: TaskQueueScheduler.DS_Schedule_SearchMicroflows] [Class: `action-btn-pagination action-btn-sm import__main min-height-table-2 select__microflow`]
      ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_Schedule_Microflow_Select`
    - 📊 **Column**: Name [Width: 50]
    - 📊 **Column**: Complete name [Width: 50]
    ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_Schedule_SelectionCancel`
