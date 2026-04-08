# Page: EmailConnector_Overview

**Allowed Roles:** Email_Connector.EmailConnectorAdmin

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataView** [Context]
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
