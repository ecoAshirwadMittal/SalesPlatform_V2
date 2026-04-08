# Page: Offer_UpdateSnowflake

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesRep

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context] [DP: {Spacing bottom: Outer large}]
  - 🔤 **Text**: "Period"
  - 📝 **DatePicker**: datePicker1
  - 📝 **DatePicker**: datePicker2
    ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_Offers_UpdateOfferStatusSnowflake`
    ↳ [acti] → **Cancel Changes**
