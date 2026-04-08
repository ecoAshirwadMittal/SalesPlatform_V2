# Page: PWSConstants_Overview

**Allowed Roles:** EcoATM_PWS.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [acti] → **Page**: `AuctionUI.Business_PWS_ControlCenter`
- 📦 **DataView** [MF: EcoATM_PWS.DS_GetOrCreatePWSConstants]
  - 🧩 **Switch** (ID: `com.mendix.widget.custom.switch.Switch`)
      - booleanAttribute: [Attr: EcoATM_PWS.PWSConstants.SendFirstReminder]
  - 🧩 **Switch** (ID: `com.mendix.widget.custom.switch.Switch`)
      - booleanAttribute: [Attr: EcoATM_PWS.PWSConstants.SendSecondReminder]
    ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_SavePWSConstants`
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
