# Page: Schedule_NewEdit_Configuration

**Allowed Roles:** TaskQueueScheduler.Configurator

**Layout:** `TaskQueueScheduler.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
    ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_Schedule_ClearQueuedActions`
  - 📝 **ReferenceSelector**: referenceSelector2 [Class: `required`]
    ↳ [Change] → **Nanoflow**: `TaskQueueScheduler.OCh_Schedule_TaskQueue`
  - ⚡ **Button**: radioButtons3
  - 📝 **DatePicker**: datePicker3 🔒 [Read-Only]
  - 📝 **DatePicker**: datePicker6 🔒 [Read-Only]
  - 📝 **DatePicker**: datePicker7 🔒 [Read-Only]
  - 📦 **DataView** [Context]
    - ⚡ **Button**: radioButtons2
    - 📝 **DatePicker**: datePicker2
    - 📝 **DropDown**: dropDown1 ✏️ (Editable if: `not($dataView1/Active)`) [Class: `required`]
    - 📝 **DatePicker**: datePicker5 ✏️ (Editable if: `not($dataView1/Active)`)
    - 📝 **DatePicker**: datePicker4
    ↳ [acti] → **Cancel Changes**
    ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_SAVESchedule`
