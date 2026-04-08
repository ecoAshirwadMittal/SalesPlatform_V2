# Nanoflow: BidData_Import_ClientController

**Allowed Roles:** EcoATM_BidData.User

## 📥 Inputs

- **$BidUploadPageHelper** (EcoATM_BidData.BidUploadPageHelper)
- **$BidRound** (AuctionUI.BidRound)
- **$XMLDocumentTemplate** (ExcelImporter.XMLDocumentTemplate)
- **$BidderRouterHelper** (AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

## ⚙️ Execution Flow

1. **DB Retrieve **EcoATM_BuyerManagement.BuyerCodeSelect_Helper**  (Result: **$BuyerCodeSelect_HelperList**)**
2. **Delete **$BuyerCodeSelect_HelperList** from Database**
3. **Retrieve related **NP_BuyerCodeSelect_Helper_SchedulingAuction** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$SchedulingAuction**)**
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
            2. **Call Microflow **EcoATM_BidData.BidData_TransformAndCommit** (Result: **$isValid_BidDataToCommit**)**
            3. **Update **$BidUploadPageHelper** (and Save to DB)
      - Set **ProgressValue** = `100`**
            4. **Close current page/popup**
            5. **Call Microflow **AuctionUI.SUB_AuctionTimerHelper** (Result: **$AuctionTimerHelper**)**
            6. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BuyerCodeSelect_Helper_BuyerCode** = `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode`**
            7. **Commit/Save **$isValid_BidDataToCommit** to Database**
            8. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$BuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`**
            9. **Create **EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper** (Result: **$NewParent_NPBuyerCodeSelectHelper**)**
            10. **Create **EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper** (Result: **$NewNP_BuyerCodeSelect_Helper**)
      - Set **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** = `$NewParent_NPBuyerCodeSelectHelper`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`**
            11. **Open Page: **EcoATM_BuyerManagement.PG_Bidder_Dashboard_HOT****
            12. **Open Page: **EcoATM_BidData.BidImportComplete****
            13. **LogMessage (Warning)**
            14. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update **$BidUploadPageHelper** (and Save to DB)
      - Set **ProgressValue** = `100`
      - Set **Status** = `EcoATM_BidData.enum_BidUploadStatus.UploadInvalid`
      - Set **FileUploadMessage** = `'Invalid data in bid file.'`**
            2. **LogMessage (Warning)**
            3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$BidUploadPageHelper** (and Save to DB)
      - Set **ProgressValue** = `70`
      - Set **Status** = `EcoATM_BidData.enum_BidUploadStatus.FileUploadError`**
      2. **LogMessage (Warning)**
      3. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
