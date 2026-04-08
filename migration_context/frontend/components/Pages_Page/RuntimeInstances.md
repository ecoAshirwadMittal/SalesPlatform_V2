# Page: RuntimeInstances

**Allowed Roles:** Administration.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataGrid** [Context]
  - 📊 **Column**: Runtime ID [Width: 30]
  - 📊 **Column**: Created [Width: 10]
  - 📊 **Column**: Allowed concurrent users [Width: 20]
  - 📊 **Column**: Partner [Width: 20]
  - 📊 **Column**: Customer [Width: 20]
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
