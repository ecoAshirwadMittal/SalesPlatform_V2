# Microflow Detailed Specification: ACT_BidData_SelectImportSheet_TEST

### 📥 Inputs (Parameters)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **Close current page/popup**
3. **Retrieve related **NP_BuyerCodeSelect_Helper_BidRound** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$BidRound**)**
4. **DB Retrieve **AuctionUI.BidData_ImportSettings**  (Result: **$BidData_ImportSettings**)**
5. 🔀 **DECISION:** `$BidData_ImportSettings != empty`
   ➔ **If [true]:**
      1. **Retrieve related **BidData_ImportSettings_DefaultTemplate** via Association from **$BidData_ImportSettings** (Result: **$BidImport_DefaultTemplate**)**
      2. **Create **ExcelImporter.XMLDocumentTemplate** (Result: **$NewXMLDocumentTemplate**)
      - Set **XMLDocumentTemplate_Template** = `$BidImport_DefaultTemplate`**
      3. **Update **$BidRound**
      - Set **BidRound_XMLDocumentTemplate** = `$NewXMLDocumentTemplate`**
      4. **Create **AuctionUI.BidUploadPageHelper** (Result: **$NewBidUploadPageHelper**)
      - Set **Status** = `EcoATM_BidData.enum_BidUploadStatus._New`
      - Set **AuctionTitle** = `$BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionTitle`**
      5. **Retrieve related **NP_BuyerCodeSelect_Helper_SchedulingAuction** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$SchedulingAuction**)**
      6. **Maps to Page: **EcoATM_BidData.PG_BidData_XMLUpload_BidRound****
      7. **LogMessage**
      8. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **LogMessage**
      2. **Show Message (Error): `An error occurred, please contact your system administrator.`**
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.