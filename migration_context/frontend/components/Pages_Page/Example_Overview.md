# Page: Example_Overview

**Allowed Roles:** OQL.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📑 **TabContainer**
  - 📑 **Tab**: "Tests"
    - 📦 **DataGrid** [Context] [Class: `datagrid-hover`]
      - ⚡ **Button**: Search [Style: Default]
      - ⚡ **Button**: Create Example Content [Style: Info]
        ↳ [acti] → **Microflow**: `OQL.IVK_CreateContent`
      - ⚡ **Button**: Select all through OQL [Style: Success]
        ↳ [acti] → **Page**: `OQL.ExampleOQL_Result`
      - ⚡ **Button**: New [Style: Default]
      - ⚡ **Button**: Edit [Style: Default]
        ↳ [acti] → **Page**: `OQL.ExamplePerson_NewEdit`
      - ⚡ **Button**: Delete [Style: Default]
        ↳ [acti] → **Delete**
      - ⚡ **Button**: Perform test [Style: Warning]
        ↳ [acti] → **Microflow**: `OQL.IVK_PerformTests`
      - 📊 **Column**: Number [Width: 22]
      - 📊 **Column**: Name [Width: 22]
      - 📊 **Column**: Birthday [Width: 28]
      - 📊 **Column**: Married to [Width: 14]
      - 📊 **Column**: Height in decimal [Width: 14]
  - 📑 **Tab**: "OQL Interface"
