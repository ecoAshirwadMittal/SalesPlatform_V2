# Page: PG_ErrorTestPage

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [acti] → **Microflow**: `AuctionUI.ACT_GenerateError`
  ↳ [acti] → **Nanoflow**: `AuctionUI.ACT_GenerateTimeOutError`
- 📦 **DataView** [MF: AuctionUI.DS_CreateDataDogTest]
    ↳ [acti] → **Save Changes**
    ↳ [acti] → **Cancel Changes**
