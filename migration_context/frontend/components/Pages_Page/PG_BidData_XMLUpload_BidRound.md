# Page: PG_BidData_XMLUpload_BidRound

**Allowed Roles:** EcoATM_BidData.Administrator, EcoATM_BidData.User

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📦 **DataView** [Context]
    - 📦 **DataView** [MF: Custom_Excel_Import.DS_ImportFile_Create]
        ↳ [acti] → **Microflow**: `EcoATM_BidData.ACT_BidData_ExcelImport`
