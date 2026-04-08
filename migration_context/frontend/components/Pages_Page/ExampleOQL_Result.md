# Page: ExampleOQL_Result

**Allowed Roles:** OQL.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📑 **TabContainer**
  - 📑 **Tab**: "By statement"
    - 📦 **DataGrid** [MF: OQL.DS_ExampleOQL_Statement]
      - 📊 **Column**: Number [Width: 11]
      - 📊 **Column**: Name [Width: 11]
      - 📊 **Column**: Date of birth [Width: 11]
      - 📊 **Column**: Age [Width: 11]
      - 📊 **Column**: Long age [Width: 22]
      - 📊 **Column**: Active [Width: 11]
      - 📊 **Column**: Height in decimal [Width: 11]
      - 📊 **Column**: Gender [Width: 12]
  - 📑 **Tab**: "By dataset"
    - 📦 **DataGrid** [MF: OQL.DS_ExampleOQL_DataSet]
      - 📊 **Column**: Number [Width: 11]
      - 📊 **Column**: Name [Width: 11]
      - 📊 **Column**: Date of birth [Width: 11]
      - 📊 **Column**: Age [Width: 11]
      - 📊 **Column**: Long age [Width: 22]
      - 📊 **Column**: Active [Width: 11]
      - 📊 **Column**: Height in decimal [Width: 11]
      - 📊 **Column**: Gender [Width: 12]
- 📦 **DataView** [Context]
  - 📝 **DatePicker**: datePicker1
  - ⚡ **Button**: radioButtons1
  - ⚡ **Button**: radioButtons2
  - 📝 **ReferenceSelector**: referenceSelector1
  - 📝 **ReferenceSelector**: referenceSelector2
    ↳ [acti] → **Save Changes**
    ↳ [acti] → **Cancel Changes**
