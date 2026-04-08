# Page: PG_BuyerSubmitConfig

**Allowed Roles:** EcoATM_BuyerManagement.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [acti] → **Page**: `AuctionUI.Business_Auctions_ControlCenter`
- 📦 **DataView** [MF: EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig]
  - ⚡ **Button**: radioButtons_3
  - ⚡ **Button**: radioButtons1
  - ⚡ **Button**: radioButtons_1
  - ⚡ **Button**: radioButtons4
  - ⚡ **Button**: radioButtons5
  - ⚡ **Button**: radioButtons6
  - ⚡ **Button**: radioButtons2
  - ⚡ **Button**: radioButtons7
  - ⚡ **Button**: radioButtons8
  - ⚡ **Button**: radioButtons9
  - ⚡ **Button**: radioButtons10
  - ⚡ **Button**: radioButtons11
  - 📂 **GroupBox**: "Auction Schedule Settings"
    ↳ [acti] → **Microflow**: `EcoATM_BuyerManagement.ACT_SaveAuctionConfiguration`
    ↳ [acti] → **Cancel Changes**
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
