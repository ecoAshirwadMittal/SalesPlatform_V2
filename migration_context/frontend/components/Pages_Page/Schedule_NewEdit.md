# Page: Schedule_NewEdit

**Allowed Roles:** TaskQueueScheduler.Configurator

**Layout:** `TaskQueueScheduler.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📝 **ReferenceSelector**: referenceSelector2 [Class: `required`]
  - ⚡ **Button**: radioButtons3
  - 📝 **DatePicker**: datePicker8 🔒 [Read-Only]
  - 📝 **DatePicker**: datePicker9 🔒 [Read-Only]
  - 📝 **DatePicker**: datePicker10 🔒 [Read-Only]
  - ⚡ **Button**: radioButtons2
  - 📝 **DatePicker**: datePicker2
  - 📝 **DropDown**: dropDown1 ✏️ (Editable if Active) [Class: `required`]
  - 📝 **DatePicker**: datePicker5 ✏️ (Editable if Active) [Class: `required`]
  - 📝 **DatePicker**: datePicker4
    ↳ [acti] → **Cancel Changes**
    ↳ [acti] → **Microflow**: `TaskQueueScheduler.ACT_SAVESchedule`
