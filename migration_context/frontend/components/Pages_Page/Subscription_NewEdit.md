# Page: Subscription_NewEdit

**Allowed Roles:** MicrosoftGraph.Administrator

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📝 **DropDown**: dropDown1
    ↳ [acti] → **OpenLink**
  - 📝 **DatePicker**: datePicker1
    ↳ [acti] → **OpenLink**
    ↳ [acti] → **Microflow**: `MicrosoftGraph.ACT_Subscription_Create`
    ↳ [acti] → **Microflow**: `MicrosoftGraph.ACT_Subscription_Update`
    ↳ [acti] → **Cancel Changes**
