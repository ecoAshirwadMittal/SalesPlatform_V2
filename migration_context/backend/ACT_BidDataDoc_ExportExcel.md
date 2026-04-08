# Microflow Detailed Specification: ACT_BidDataDoc_ExportExcel

### 📥 Inputs (Parameters)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Update **$BidderRouterHelper**
      - Set **BuyerCode** = `$NP_BuyerCodeSelect_Helper/Code`**
3. **Retrieve related **NP_BuyerCodeSelect_Helper_SchedulingAuction** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$SchedulingAuction**)**
4. 🔀 **DECISION:** `$SchedulingAuction!= empty`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Error****
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Retrieve related **NP_BuyerCodeSelect_Helper_BuyerCode** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$BuyerCode**)**
      2. 🔀 **DECISION:** `$BuyerCode != empty`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_BuyerManagement.SUB_BidRound_GetCurrentBIdRound** (Result: **$BidRound**)**
            2. 🔀 **DECISION:** `$BidRound != empty`
               ➔ **If [false]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_Error****
                  2. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_BuyerManagement.SUB_GetBidDataExportTemplate** (Result: **$MxTemplate**)**
                  2. 🔀 **DECISION:** `$MxTemplate != empty`
                     ➔ **If [false]:**
                        1. **Call Microflow **Custom_Logging.SUB_Log_Error****
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Call Microflow **AuctionUI.ACT_BidDataDoc_GetOrCreate** (Result: **$BidDataDoc**)**
                        2. **Retrieve related **BidData_BidDataDoc** via Association from **$BidDataDoc** (Result: **$BidDataList**)**
                        3. 🔀 **DECISION:** `$BidDataDoc != empty`
                           ➔ **If [true]:**
                              1. **Call Microflow **EcoATM_BuyerManagement.SUB_BidDataCustomExcelExport****
                              2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                              3. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **Call Microflow **Custom_Logging.SUB_Log_Error****
                              2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Error****
            2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.