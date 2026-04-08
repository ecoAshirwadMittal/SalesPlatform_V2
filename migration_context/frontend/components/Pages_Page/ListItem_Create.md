# Page: ListItem_Create

**Layout:** `Sharepoint.DefaultLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📦 **DataGrid** [Association: undefined]
      ↳ [acti] → **Page**: `Sharepoint.Field_NewEdit`
    - ⚡ **Button**: New [Style: Default]
      ↳ [acti] → **Delete**
    - 📊 **Column**: Name [Width: 14]
    - 📊 **Column**: Column type [Width: 14]
    - 📊 **Column**: Display value [Width: 14]
    - 📊 **Column**: String value [Width: 14]
    - 📊 **Column**: Numeric value [Width: 14]
    - 📊 **Column**: Date time value [Width: 15]
    - 📊 **Column**: Boolean value [Width: 15]
    ↳ [acti] → **Microflow**: `Sharepoint.ACT_CreateListItemCreate`
    ↳ [acti] → **Cancel Changes**
