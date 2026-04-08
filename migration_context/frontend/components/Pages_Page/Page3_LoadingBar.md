# Page: Page3_LoadingBar

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

**Layout:** `AuctionUI.ecoATM_Popup_Layout_NoTitle`

## Widget Tree

- 📦 **DataView** [Context]
  - 🧩 **Progress Bar** [Class: `border` | Style: `border-radius:2px` | DP: {Spacing top: Outer large}] (ID: `com.mendix.widget.custom.progressbar.ProgressBar`)
      - type: expression
      - staticCurrentValue: 50
      - expressionCurrentValue: `$FileUploadProcessHelper/CurrentPercentage
`
      - staticMinValue: 0
      - expressionMinValue: `0`
      - staticMaxValue: 100
      - expressionMaxValue: `100`
      - labelType: text
    ↳ [acti] → **Close Page**
    ↳ [acti] → **Close Page**
