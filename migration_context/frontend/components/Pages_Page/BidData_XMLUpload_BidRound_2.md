# Page: BidData_XMLUpload_BidRound_2

**Allowed Roles:** EcoATM_BidData.Administrator, EcoATM_BidData.User

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataView** [Context]
  - 📦 **DataView** [Context]
    - 📦 **DataView** [Context]
        ↳ [acti] → **Microflow**: `EcoATM_BidData.ACT_BidData_Import_ClientController`
        ↳ [acti] → **Microflow**: `EcoATM_BidData.ACT_BidIMport_Cancel`
      - 🧩 **Progress Bar** [Class: `border` | Style: `border-radius:2px;` | DP: {Spacing top: Outer large}] (ID: `com.mendix.widget.custom.progressbar.ProgressBar`)
          - type: expression
          - staticCurrentValue: 50
          - expressionCurrentValue: `$BidUploadPageHelper/ProgressValue`
          - staticMinValue: 0
          - expressionMinValue: `0`
          - staticMaxValue: 100
          - expressionMaxValue: `100`
          - labelType: text
