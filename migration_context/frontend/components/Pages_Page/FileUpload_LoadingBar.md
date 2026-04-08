# Page: FileUpload_LoadingBar

**Allowed Roles:** EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesLeader, EcoATM_RMA.SalesOps, EcoATM_RMA.SalesRep

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🧩 **Progress Bar** [Class: `rma-file-upload-progressbar` | DP: {Spacing top: Outer large, Striped bar: [object Object]}] (ID: `com.mendix.widget.custom.progressbar.ProgressBar`)
      - type: expression
      - staticCurrentValue: 50
      - expressionCurrentValue: `$currentObject/CurrentPercentage`
      - staticMinValue: 0
      - expressionMinValue: `0`
      - staticMaxValue: 100
      - expressionMaxValue: `100`
      - labelType: text
