# Microflow Detailed Specification: ACT_DownloadRound3File

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/Round=3` (Result: **$Round3SchedulingAuction**)**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Call Microflow **EcoATM_BuyerManagement.SUB_BidRound_GetCurrentBIdRound** (Result: **$BidRound**)**
5. 🔀 **DECISION:** `$BidRound != empty`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Error****
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='BidDataExport']` (Result: **$MxTemplate**)**
      2. 🔀 **DECISION:** `$MxTemplate != empty`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Error****
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Call Microflow **AuctionUI.ACT_BidDataDoc_GetOrCreate** (Result: **$BidDataDoc**)**
            2. 🔀 **DECISION:** `$BidDataDoc != empty`
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_BuyerManagement.SUB_BidDataCustomExcelExport_PopulateDocWithoutDownload_PreRound3****
                  2. **DownloadFile**
                  3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_Error****
                  2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.