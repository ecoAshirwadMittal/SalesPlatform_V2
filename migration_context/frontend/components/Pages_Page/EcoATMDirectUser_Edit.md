# Page: EcoATMDirectUser_Edit

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🔤 **Text**: "Last invite sent" [Class: `control-label-standalone`]
  - 🔤 **Text**: "Activation Date" [Class: `control-label-standalone`]
  - 🔤 **Text**: "Last Login" [Class: `control-label-standalone`]
  - 📝 **DropDown**: dropDown1 [DP: {Spacing left: Outer small}] 👁️ (If Inactive is true/false)
  - 📝 **DropDown**: dropDown2
  - 🔤 **Text**: "Role" [Class: `control-label-standalone`]
  - 🧩 **Checkbox Set Selector** [Class: `border`] (ID: `CheckboxSelector.widget.checkboxselector`)
      - sortAttr: [Attr: System.UserRole.Name]
      - sortOrder: Asc
      - limit: 0
      ➤ **displayAttrs**
          - header: Role
          - displayAttr: [Attr: System.UserRole.Name]
          - displayWidth: 90
          - decimalPrecision: 2
          - currency: None
      - onChangeMf: [MF: AuctionUI.ACT_SetBuyerVisibility]
    ↳ [acti] → **Cancel Changes**
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_SaveUserChanges`
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_ResendInvite`
