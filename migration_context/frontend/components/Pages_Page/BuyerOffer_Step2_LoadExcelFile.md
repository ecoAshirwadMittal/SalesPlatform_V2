# Page: BuyerOffer_Step2_LoadExcelFile

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

**Layout:** `AuctionUI.ecoATM_Popup_Layout_NoTitle`

## Widget Tree

- 📦 **DataView** [Context]
  - 🧩 **Progress Bar** [Class: `border` | Style: `border-radius:2px` | DP: {Spacing top: Outer large}] (ID: `com.mendix.widget.custom.progressbar.ProgressBar`)
      - type: expression
      - staticCurrentValue: 50
      - expressionCurrentValue: `$OfferExcelImportDocument/ProcessPercentage
`
      - staticMinValue: 0
      - expressionMinValue: `0`
      - staticMaxValue: 100
      - expressionMaxValue: `100`
      - labelType: text
    ↳ [acti] → **Nanoflow**: `EcoATM_PWS.NAN_UploadExcelFileContent_Close`
    ↳ [acti] → **Nanoflow**: `EcoATM_PWS.NAN_UploadExcelFileContent_Close`
