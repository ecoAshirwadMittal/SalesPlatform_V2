# Microflow Detailed Specification: ACT_BidDataDoc_PopulateExcelDoc_PreRound3

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Call Microflow **EcoATM_BuyerManagement.SUB_BidRound_GetCurrentBIdRound** (Result: **$BidRound**)**
3. 🔀 **DECISION:** `$BidRound != empty`
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
                  2. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_CreateDriveItemCreate** (Result: **$SharepointSuccess**)**
                  3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  4. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_Error****
                  2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.