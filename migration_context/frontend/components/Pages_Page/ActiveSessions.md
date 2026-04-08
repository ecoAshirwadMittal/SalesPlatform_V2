# Page: ActiveSessions

**Allowed Roles:** Administration.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataGrid** [Context]
  - ⚡ **Button**: Search [Style: Default]
  - ⚡ **Button**: Logout session [Style: Default]
    ↳ [acti] → **Delete**
  - 📊 **Column**: User name [Width: 75]
  - 📊 **Column**: Last active [Width: 25]
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
