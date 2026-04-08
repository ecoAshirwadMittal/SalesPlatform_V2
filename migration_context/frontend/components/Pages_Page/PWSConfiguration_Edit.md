# Page: PWSConfiguration_Edit

**Allowed Roles:** EcoATM_PWSIntegration.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

  ↳ [acti] → **Page**: `AuctionUI.Business_PWS_ControlCenter`
- 📦 **DataView** [MF: EcoATM_PWSIntegration.DS_PWSConfiguration]
  - 📑 **TabContainer**
    - 📑 **Tab**: "Security"
        ↳ [acti] → **Microflow**: `EcoATM_PWSIntegration.ACT_PWSConfiguration_TestAuthentication`
      - 📝 **CheckBox**: checkBox1
      - 📝 **CheckBox**: checkBox2
    - 📑 **Tab**: "API"
    ↳ [acti] → **Save Changes**
    ↳ [acti] → **Close Page**
