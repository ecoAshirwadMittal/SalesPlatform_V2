# Page: Account_Overview

**Allowed Roles:** Administration.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📑 **TabContainer** [Class: `tabsfullwidth`]
  - 📑 **Tab**: "Local Users"
    - 📦 **DataGrid** [Context] [Class: `datagrid-large` | DP: {Hover style: [object Object]}]
      - ⚡ **Button**: New local user [Style: Success]
        ↳ [acti] → **Microflow**: `Administration.NewAccount`
      - ⚡ **Button**: New web service user [Style: Default]
        ↳ [acti] → **Microflow**: `Administration.NewWebServiceAccount`
      - ⚡ **Button**: Edit [Style: Default]
        ↳ [acti] → **Page**: `Administration.Account_Edit`
      - ⚡ **Button**: Delete [Style: Danger]
        ↳ [acti] → **Delete**
      - 📊 **Column**: Full name [Width: 20]
      - 📊 **Column**: Login [Width: 20]
      - 📊 **Column**: Roles [Width: 20]
      - 📊 **Column**: Last login [Width: 10]
      - 📊 **Column**: Active [Width: 10]
      - 📊 **Column**: Web service user [Width: 10]
      - 📊 **Column**: Local [Width: 10]
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
