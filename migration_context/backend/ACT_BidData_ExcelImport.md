# Microflow Detailed Specification: ACT_BidData_ExcelImport

### 📥 Inputs (Parameters)
- **$BidUploadPageHelper** (Type: AuctionUI.BidUploadPageHelper)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$ImportFile** (Type: Custom_Excel_Import.ImportFile)
- **$Parent_NPBuyerCodeSelectHelper** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **NP_BuyerCodeSelect_Helper_SchedulingAuction** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$SchedulingAuction**)**
2. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
3. **Update **$ImportFile**
      - Set **ErrorMessage** = `empty`**
4. 🔀 **DECISION:** `$ImportFile/Name != empty`
   ➔ **If [true]:**
      1. **LogMessage**
      2. **Call Microflow **EcoATM_BidData.SUB_GetBidImportSheetName** (Result: **$SheetName**)**
      3. 🔀 **DECISION:** `$SheetName='BidDataExport'`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_BidData.SUB_BidDataImport_NoRank****
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$SheetName='BidDataRankRound2Export'`
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_BidData.SUB_BidDataImport_Round2BidRank****
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$SheetName='BidDataRankRound3Export'`
                     ➔ **If [true]:**
                        1. **Call Microflow **EcoATM_BidData.SUB_BidDataImport_Round3BidRank****
                        2. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Update **$ImportFile**
      - Set **ErrorMessage** = `'No Bid Import Template found'`**
                        2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$ImportFile**
      - Set **ErrorMessage** = `'Import file cannot be empty, please select a file and try again.'`**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.