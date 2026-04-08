# Page: QueuedAction_Overview

**Allowed Roles:** TaskQueueScheduler.Configurator

**Layout:** `TaskQueueScheduler.Atlas_Default`

## Widget Tree

- 📦 **DataGrid** [Context]
  - 📊 **Column**: Queue name [Width: 22]
  - 📊 **Column**: Microflow name [Width: 23]
  - 📊 **Column**: Running [Width: 10]
  - 📊 **Column**: Start time [Width: 11]
  - 📊 **Column**: Finish time [Width: 11]
  - 📊 **Column**: Reference text [Width: 23]
- 📦 **DataView** [Context]
  - 📦 **DataGrid** [Context]
    - 📊 **Column**: Reference text [Width: 24]
    - 📊 **Column**: Count [Width: 12]
    - 📊 **Column**: Batch size [Width: 12]
    - 📊 **Column**: Offset [Width: 13]
    - 📊 **Column**: Param1 [Width: 13]
    - 📊 **Column**: Param2 [Width: 13]
    - 📊 **Column**: Param3 [Width: 13]
