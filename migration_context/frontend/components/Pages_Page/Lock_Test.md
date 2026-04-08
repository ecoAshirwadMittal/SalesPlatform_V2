# Page: Lock_Test

**Allowed Roles:** EcoATM_Lock.Admin, EcoATM_Lock.User

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataView** [Context]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 30000
      - callEvent: callMicroflow
      - microflow: [MF: EcoATM_Lock.ACT_Lock_Refresh]
    ↳ [acti] → **Cancel Changes**
