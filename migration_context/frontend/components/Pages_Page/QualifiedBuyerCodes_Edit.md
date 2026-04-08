# Page: QualifiedBuyerCodes_Edit

**Allowed Roles:** EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.SalesOps, EcoATM_BuyerManagement.SalesRep

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - ⚡ **Button**: radioButtons1
  - ⚡ **Button**: radioButtons2
  - ⚡ **Button**: radioButtons3
    ↳ [acti] → **Microflow**: `EcoATM_BuyerManagement.ACT_OverrideQualificationBuyerCodes`
    ↳ [acti] → **Cancel Changes**
