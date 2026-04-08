# Page: UserHelperGuide_NewEdit

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context] [DP: {Spacing bottom: Outer large}]
  - 📝 **DropDown**: dropDown1
  - 📝 **CheckBox**: checkBox1
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_SaveDocument`
    ↳ [acti] → **Cancel Changes**
