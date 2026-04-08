# Page: TaskQueue_Overview

**Allowed Roles:** TaskQueueScheduler.Configurator

**Layout:** `TaskQueueScheduler.Atlas_Default`

## Widget Tree

- 📦 **DataGrid** [Context]
  - ⚡ **Button**: New [Style: Default]
    ↳ [acti] → **Page**: `TaskQueueScheduler.TaskQueue_NewEdit`
    ↳ [acti] → **Delete**
  - ⚡ **Button**: Export to Excel [Style: Default]
  - 📊 **Column**: Name [Width: 25]
  - 📊 **Column**: Full name [Width: 25]
  - 📊 **Column**: Short name [Width: 13]
  - 📊 **Column**: Allow scheduling [Width: 12]
  - 📊 **Column**: Description [Width: 25]
