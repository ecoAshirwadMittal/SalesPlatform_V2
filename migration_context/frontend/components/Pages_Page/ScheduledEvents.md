# Page: ScheduledEvents

**Allowed Roles:** Administration.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataGrid** [Context]
  - ⚡ **Button**: Search [Style: Default]
  - 📊 **Column**: Name [Width: 20]
  - 📊 **Column**: Description [Width: 35]
  - 📊 **Column**: Start time [Width: 20]
  - 📊 **Column**: column4 [Width: 5]
  - 📊 **Column**: End time [Width: 20]
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
