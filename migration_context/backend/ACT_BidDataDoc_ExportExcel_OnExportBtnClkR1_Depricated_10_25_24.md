# Microflow Detailed Specification: ACT_BidDataDoc_ExportExcel_OnExportBtnClkR1_Depricated_10_25_24

### 📥 Inputs (Parameters)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_Caching.NP_BuyerCodeSelect_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **Update **$BidderRouterHelper**
      - Set **BuyerCode** = `$NP_BuyerCodeSelect_Helper/Code`**
3. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='BidData']` (Result: **$MxTemplate**)**
4. 🔀 **DECISION:** `$MxTemplate!=empty`
   ➔ **If [true]:**
      1. **Create **AuctionUI.BidDataDoc** (Result: **$NewBidDataDoc**)
      - Set **DeleteAfterDownload** = `true`**
      2. **Create **AuctionUI.ClickedRound** (Result: **$ClickedRound**)
      - Set **Round** = `1`**
      3. **Call Microflow **ECOATM_Buyer.SUB_GetBidDownload_Helper_Depricated_10_28_24** (Result: **$BidDownload_HelperList**)**
      4. **Commit/Save **$BidDownload_HelperList** to Database**
      5. **Call Microflow **ECOATM_Buyer.SUB_BidDataCustomExcelExport****
      6. **LogMessage**
      7. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.