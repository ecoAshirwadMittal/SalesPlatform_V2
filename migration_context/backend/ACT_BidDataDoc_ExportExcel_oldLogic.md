# Microflow Detailed Specification: ACT_BidDataDoc_ExportExcel_oldLogic

### 📥 Inputs (Parameters)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_Caching.NP_BuyerCodeSelect_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **Update **$BidderRouterHelper**
      - Set **BuyerCode** = `$NP_BuyerCodeSelect_Helper/Code`**
3. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='BidDataExport']` (Result: **$MxTemplate**)**
4. 🔀 **DECISION:** `$MxTemplate!=empty`
   ➔ **If [true]:**
      1. **Create **AuctionUI.BidDataDoc** (Result: **$NewBidDataDoc**)
      - Set **DeleteAfterDownload** = `true`**
      2. **Create **AuctionUI.ClickedRound** (Result: **$ClickedRound**)
      - Set **Round** = `1`**
      3. **Retrieve related **NP_BuyerCodeSelect_Helper_SchedulingAuction** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$SchedulingAuction**)**
      4. **LogMessage**
      5. **Retrieve related **NP_BuyerCodeSelect_Helper_BuyerCode** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$BuyerCode**)**
      6. **LogMessage**
      7. **Call Microflow **AuctionUI.SUB_BidRound_CreateNew** (Result: **$BidRound**)**
      8. **LogMessage**
      9. **Call Microflow **AuctionUI.SUB_GetCurrentWeek** (Result: **$Week**)**
      10. **LogMessage**
      11. **Call Microflow **AuctionUI.ACT_CreateBidDataHelper_AggregatedList** (Result: **$AgregatedInventory**)**
      12. 🔀 **DECISION:** `$AgregatedInventory != empty`
         ➔ **If [true]:**
            1. **Call Microflow **AuctionUI.SUB_BidDownloadHelper_CreateList** (Result: **$BidDownload_HelperList**)**
            2. 🔀 **DECISION:** `$BidDownload_HelperList != empty`
               ➔ **If [false]:**
                  1. **LogMessage**
                  2. **Commit/Save **$BidDownload_HelperList** to Database**
                  3. **Call Microflow **AuctionUI.SUB_BidDataCustomExcelExport****
                  4. **LogMessage**
                  5. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='BidDataTest']` (Result: **$MxTemplate_1**)**
                  6. 🔀 **DECISION:** `$MxTemplate_1 != empty`
                     ➔ **If [false]:**
                        1. **LogMessage**
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Retrieve related **BidRound_BidDataDoc** via Association from **$BidRound** (Result: **$BidDataDoc**)**
                        2. **Retrieve related **BidData_BidDataDoc** via Association from **$BidDataDoc** (Result: **$BidDataList**)**
                        3. **Call Microflow **AuctionUI.SUB_BidDataCustomExcelExport****
                        4. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Commit/Save **$BidDownload_HelperList** to Database**
                  2. **Call Microflow **AuctionUI.SUB_BidDataCustomExcelExport****
                  3. **LogMessage**
                  4. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='BidDataTest']` (Result: **$MxTemplate_1**)**
                  5. 🔀 **DECISION:** `$MxTemplate_1 != empty`
                     ➔ **If [false]:**
                        1. **LogMessage**
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Retrieve related **BidRound_BidDataDoc** via Association from **$BidRound** (Result: **$BidDataDoc**)**
                        2. **Retrieve related **BidData_BidDataDoc** via Association from **$BidDataDoc** (Result: **$BidDataList**)**
                        3. **Call Microflow **AuctionUI.SUB_BidDataCustomExcelExport****
                        4. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **LogMessage**
            2. **Call Microflow **AuctionUI.SUB_BidDownloadHelper_CreateList** (Result: **$BidDownload_HelperList**)**
            3. 🔀 **DECISION:** `$BidDownload_HelperList != empty`
               ➔ **If [false]:**
                  1. **LogMessage**
                  2. **Commit/Save **$BidDownload_HelperList** to Database**
                  3. **Call Microflow **AuctionUI.SUB_BidDataCustomExcelExport****
                  4. **LogMessage**
                  5. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='BidDataTest']` (Result: **$MxTemplate_1**)**
                  6. 🔀 **DECISION:** `$MxTemplate_1 != empty`
                     ➔ **If [false]:**
                        1. **LogMessage**
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Retrieve related **BidRound_BidDataDoc** via Association from **$BidRound** (Result: **$BidDataDoc**)**
                        2. **Retrieve related **BidData_BidDataDoc** via Association from **$BidDataDoc** (Result: **$BidDataList**)**
                        3. **Call Microflow **AuctionUI.SUB_BidDataCustomExcelExport****
                        4. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Commit/Save **$BidDownload_HelperList** to Database**
                  2. **Call Microflow **AuctionUI.SUB_BidDataCustomExcelExport****
                  3. **LogMessage**
                  4. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='BidDataTest']` (Result: **$MxTemplate_1**)**
                  5. 🔀 **DECISION:** `$MxTemplate_1 != empty`
                     ➔ **If [false]:**
                        1. **LogMessage**
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Retrieve related **BidRound_BidDataDoc** via Association from **$BidRound** (Result: **$BidDataDoc**)**
                        2. **Retrieve related **BidData_BidDataDoc** via Association from **$BidDataDoc** (Result: **$BidDataList**)**
                        3. **Call Microflow **AuctionUI.SUB_BidDataCustomExcelExport****
                        4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.