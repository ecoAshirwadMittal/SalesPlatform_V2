# Page: ProcessedQueueTask_ViewRetries

**Allowed Roles:** TaskQueueHelpers.Administrator

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📦 **DataGrid** [MF: TaskQueueHelpers.DS_RetrieveTaskRetries] [DP: {Hover style: [object Object], Style: Striped, Spacing bottom: Outer large}]
      ↳ [acti] → **Page**: `TaskQueueHelpers.ProcessedQueueTask_View`
    - 📊 **Column**: Sequence [Width: 2]
    - 📊 **Column**: Status [Width: 4]
    - 📊 **Column**: Queue name [Width: 9]
    - 📊 **Column**: Context type [Width: 4]
    - 📊 **Column**: User name [Width: 9]
    - 📊 **Column**: Microflow name [Width: 15]
    - 📊 **Column**: User action name [Width: 15]
    - 📊 **Column**: XAS ID [Width: 5]
    - 📊 **Column**: Thread ID [Width: 5]
    - 📊 **Column**: Created [Width: 10]
    - 📊 **Column**: Started [Width: 10]
    - 📊 **Column**: Retried [Width: 2]
    - 📊 **Column**: Error message [Width: 10]
