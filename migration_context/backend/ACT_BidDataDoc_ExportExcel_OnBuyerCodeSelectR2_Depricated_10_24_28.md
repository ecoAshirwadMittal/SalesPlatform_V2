# Microflow Detailed Specification: ACT_BidDataDoc_ExportExcel_OnBuyerCodeSelectR2_Depricated_10_24_28

### 📥 Inputs (Parameters)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_Caching.NP_BuyerCodeSelect_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='BidData']` (Result: **$MxTemplate**)**
2. 🔀 **DECISION:** `$MxTemplate!=empty`
   ➔ **If [true]:**
      1. **Create Variable **$FileName** = `$NP_BuyerCodeSelect_Helper/Code+'_'+ trim(replaceAll(replaceAll($BidderRouterHelper/CurrYearWeek, ' ', ''),'/','_'))+ '_'+'Round2.xlsx'`**
      2. **Create **AuctionUI.BidDataDoc** (Result: **$NewBidDataDoc**)
      - Set **DeleteAfterDownload** = `true`
      - Set **Name** = `$FileName`**
      3. **Create **AuctionUI.ClickedRound** (Result: **$ClickedRound**)
      - Set **Round** = `2`**
      4. **Call Microflow **ECOATM_Buyer.SUB_GetBidDownload_Helper** (Result: **$BidDownload_HelperList**)**
      5. **Commit/Save **$BidDownload_HelperList** to Database**
      6. **Call Microflow **ECOATM_Buyer.SUB_BidDataGenerateReport****
      7. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.