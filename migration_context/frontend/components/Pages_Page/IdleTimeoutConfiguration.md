# Page: IdleTimeoutConfiguration

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataView** [MF: EcoATM_Direct_Theme.DS_IdleTimeoutConfiguration]
  - 🧩 **Switch** (ID: `com.mendix.widget.custom.switch.Switch`)
      - booleanAttribute: [Attr: EcoATM_Direct_Theme.IdleTimeoutConfiguration.IsActive]
    ↳ [acti] → **Save Changes**
    ↳ [acti] → **Cancel Changes**
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
