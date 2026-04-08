# Page: Page2_UploadData

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context] [DP: {Spacing bottom: Outer large}]
  - 🧩 **Antd Date Picker** [Class: `pws-datepicker`] (ID: `mendix.antddatepicker.AntdDatePicker`)
      - picker: date
      - format: MMM DD, YYYY
      - value: [Attr: EcoATM_PWS.MDMFuturePriceHelper.FuturePWSPriceDate]
      - placement: bottomLeft
      - locale: en_US
      - minDate: `[%BeginOfCurrentDay%]
`
      - disableDateMode: off
      - disableSunday: `false`
      - disableMonday: `false`
      - disableTuesday: `false`
      - disableWednesday: `false`
      - disableThursday: `false`
      - disableFriday: `false`
      - disableSaturday: `false`
      - size: large
      - placeholder: New Date
      - popupClassName: pws-datepicker-popup
  - 📦 **DataView** [NF: EcoATM_PWS.DS_GetorCreatePricingUpdateFile]
    ↳ [acti] → **Nanoflow**: `EcoATM_PWS.NAV_Device_Wizard_Page3`
