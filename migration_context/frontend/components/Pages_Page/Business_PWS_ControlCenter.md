# Page: Business_PWS_ControlCenter

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [Click] → **Page**: `Eco_Core.FeatureFlag_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWSMDM.Brand_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWSIntegration.PWSResponseConfig_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWSIntegration.PWSConfiguration_Edit`
  ↳ [Click] → **Microflow**: `EcoATM_PWS.SUB_SetSLATag_Admin`
  ↳ [Click] → **Microflow**: `EcoATM_PWS.SUB_RemoveSLATagsForAllOffers`
  ↳ [Click] → **Page**: `EcoATM_PWS.PWSConstants_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWSIntegration.DeposcoConfig_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWS.OrderStatus_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWS.MaintenanceMode_Overview`
  ↳ [Click] → **Page**: `EcoATM_RMA.RMAStatus_Overview`
  ↳ [Click] → **Page**: `EcoATM_RMA.RMATemptate_Overview`
  ↳ [Click] → **Page**: `EcoATM_PWS.NavigationMenu_Overview`
  ↳ [Click] → **Microflow**: `EcoATM_PWS.ACT_Offers_UpdateSnowflake`
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
