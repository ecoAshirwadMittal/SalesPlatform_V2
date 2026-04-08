# Page: ListItem_Details

**Layout:** `Sharepoint.DefaultLayout`

## Widget Tree

- 📦 **DataView** [Context]
    ↳ [acti] → **Microflow**: `Sharepoint.ACT_DownloadItem`
  - 📑 **TabContainer**
    - 📑 **Tab**: "Properties"
      - 📦 **DataView** [Context]
      - 📝 **DatePicker**: datePicker1
      - 📝 **DatePicker**: datePicker2
    - 📑 **Tab**: "Drive Item"
      - 📦 **DataView** [Context]
    - 📑 **Tab**: "Parent Reference"
      - 📦 **DataView** [Context]
    - 📑 **Tab**: "Web"
    - 📑 **Tab**: "Fields"
      - 📦 **DataGrid** [Association: undefined]
        - 📊 **Column**: Name [Width: 15]
        - 📊 **Column**: Column type [Width: 17]
        - 📊 **Column**: Display value [Width: 68]
    ↳ [acti] → **Close Page**
