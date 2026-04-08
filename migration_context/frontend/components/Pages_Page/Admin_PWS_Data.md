# Page: Admin_PWS_Data

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [Click] → **Page**: `EcoATM_PWS.Order_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWS.Offer_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWS.OfferItem_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWSMDM.OfferID_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWSMDM.Color_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWSMDM.Brand_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWSMDM.Capacity_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWSMDM.Carrier_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWSMDM.Grade_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWSMDM.Model_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWSMDM.Category_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWSMDM.CaseLot_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWSMDM.Device_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWSIntegration.Integration_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWS.ShipmentDetail_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWS.PWSInventorySyncReport_Overview`
  ↳ [Click] → **Page**: `EcoATM_RMA.RMA_Overview`
  ↳ [Click] → **Page**: `EcoATM_RMA.RMAItem_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWSMDM.Note_Overview`
  ↳ [Click] → **Page**: `EcoATM_RMA.RMAFile_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWS.BuyerOffer_Overview`
  ↳ [Click] → **Page**: `EcoATM_Direct_Theme.IdleTimeout_Overview`
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
