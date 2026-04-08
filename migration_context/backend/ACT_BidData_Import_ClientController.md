# Microflow Detailed Specification: ACT_BidData_Import_ClientController

### 📥 Inputs (Parameters)
- **$BidUploadPageHelper** (Type: EcoATM_BidData.BidUploadPageHelper)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$XMLDocumentTemplate** (Type: ExcelImporter.XMLDocumentTemplate)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$BuyerCodeSelect_Helper** (Type: AuctionUI.BuyerCodeSelect_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **Retrieve related **BidData_Helper_BidRound** via Association from **$BidRound** (Result: **$BidData_HelperList**)**
3. **Delete**
4. **Update **$BidUploadPageHelper** (and Save to DB)
      - Set **ProgressValue** = `15`
      - Set **Status** = `EcoATM_BidData.enum_BidUploadStatus.FileLoaded`
      - Set **FileName** = `$XMLDocumentTemplate/Name`**
5. **Call Microflow **EcoATM_BidData.BidData_ImportExcel** (Result: **$isValidImport**)**
6. 🔀 **DECISION:** `$isValidImport`
   ➔ **If [true]:**
      1. **Update **$BidUploadPageHelper** (and Save to DB)
      - Set **ProgressValue** = `70`
      - Set **Status** = `EcoATM_BidData.enum_BidUploadStatus.BidDataImported`
      - Set **ProcessingMessage** = `'Validating Import'`**
      2. **Call Microflow **EcoATM_BidData.BidData_Validate** (Result: **$isValid**)**
      3. 🔀 **DECISION:** `$isValid = true`
         ➔ **If [true]:**
            1. **Update **$BidUploadPageHelper** (and Save to DB)
      - Set **ProgressValue** = `85`
      - Set **ProcessingMessage** = `'Creating Bids'`**
            2. **Call Microflow **EcoATM_BidData.BidData_TransformAndCommit** (Result: **$BidDataTo_Commit**)**
            3. **Update **$BidUploadPageHelper** (and Save to DB)
      - Set **ProgressValue** = `100`**
            4. **Close current page/popup**
            5. **Call Microflow **AuctionUI.SUB_AuctionTimerHelper** (Result: **$AuctionTimerHelper**)**
            6. **Commit/Save **$BidDataTo_Commit** to Database**
            7. **Maps to Page: **AuctionUI.Bidder_Dashboard_HOT****
            8. **Maps to Page: **EcoATM_BidData.BidImportComplete****
            9. **LogMessage**
            10. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update **$BidUploadPageHelper** (and Save to DB)
      - Set **ProgressValue** = `100`
      - Set **Status** = `EcoATM_BidData.enum_BidUploadStatus.UploadInvalid`
      - Set **FileUploadMessage** = `'Invalid data in bid file.'`**
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$BidUploadPageHelper** (and Save to DB)
      - Set **ProgressValue** = `70`
      - Set **Status** = `EcoATM_BidData.enum_BidUploadStatus.FileUploadError`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.