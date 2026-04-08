# Page: IdleTimeoutAlert

**Allowed Roles:** EcoATM_Direct_Theme.Administrator, EcoATM_Direct_Theme.User

**Layout:** `AuctionUI.ecoATM_Popup_Layout_NoTitle`

## Widget Tree

  ↳ [acti] → **Close Page**
- 📦 **DataView** [MF: EcoATM_Direct_Theme.DS_GetIdleTimeoutConfig]
  - 📦 **DataView** [NF: EcoATM_Direct_Theme.DS_CreateTimerHelper]
    - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
        - interval: 1000
        - callEvent: callNanoflow
      ↳ [acti] → **Nanoflow**: `EcoATM_Direct_Theme.ACT_ContinueSession`
      ↳ [acti] → **SignOut**
