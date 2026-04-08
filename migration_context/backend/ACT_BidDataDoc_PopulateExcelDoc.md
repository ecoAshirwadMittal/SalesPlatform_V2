# Microflow Detailed Specification: ACT_BidDataDoc_PopulateExcelDoc

### 📥 Inputs (Parameters)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$BidRound** (Type: AuctionUI.BidRound)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. 🔀 **DECISION:** `$SchedulingAuction!= empty`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Error****
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Retrieve related **BidRound_BuyerCode** via Association from **$BidRound** (Result: **$BuyerCode**)**
      2. 🔀 **DECISION:** `$BuyerCode != empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$BidRound != empty`
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
                        1. **Retrieve related **BidRound_BidDataDoc** via Association from **$BidRound** (Result: **$BidDataDoc**)**
                        2. 🔀 **DECISION:** `$BidDataDoc != empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **EcoATM_BuyerManagement.SUB_BidDataCustomExcelExport_PopulateDocWithoutDownload****
                              2. **Call Microflow **Custom_Logging.SUB_Log_Info****
                              3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **Call Microflow **Custom_Logging.SUB_Log_Error****
                              2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Error****
            2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.