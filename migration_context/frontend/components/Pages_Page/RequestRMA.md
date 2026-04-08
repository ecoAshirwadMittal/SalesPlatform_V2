# Page: RequestRMA

**Allowed Roles:** EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesOps, EcoATM_RMA.SalesRep

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [MF: EcoATM_RMA.DS_CreateRMAFile]
    ↳ [acti] → **Microflow**: `EcoATM_RMA.ACT_DownloadRMATemplate`
    ↳ [acti] → **Nanoflow**: `EcoATM_RMA.ACT_SubmitRMAFile`
