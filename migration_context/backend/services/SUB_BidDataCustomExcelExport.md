# Microflow Detailed Specification: SUB_BidDataCustomExcelExport

### 📥 Inputs (Parameters)
- **$Template** (Type: XLSReport.MxTemplate)
- **$BidDataDoc** (Type: AuctionUI.BidDataDoc)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. 🔀 **DECISION:** `trim($BidDataDoc/Name) != ''`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$BidderRouterHelper != empty`
         ➔ **If [true]:**
            1. **Create Variable **$FileName** = `$BidderRouterHelper/BuyerCode+'_'+ trim(replaceAll(replaceAll($BidderRouterHelper/CurrYearWeek, ' ', ''),'/','_'))+ '_'+ trim(replaceAll($BidderRouterHelper/CurrentRound, ' ', ''))+'.xlsx'`**
            2. **Create Variable **$BidDataDoc_Name** = `$BidDataDoc/Name`**
            3. **Create Variable **$BidDataDoc_FileID** = `toString($BidDataDoc/FileID)`**
            4. **Call Microflow **Custom_Logging.SUB_Log_Info****
            5. **JavaCallAction**
            6. **Commit/Save **$BidDataDoc** to Database**
            7. **DownloadFile**
            8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            9. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Error****
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$BidDataDoc**
      - Set **Name** = `'Missing Name'`**
      2. 🔀 **DECISION:** `$BidderRouterHelper != empty`
         ➔ **If [true]:**
            1. **Create Variable **$FileName** = `$BidderRouterHelper/BuyerCode+'_'+ trim(replaceAll(replaceAll($BidderRouterHelper/CurrYearWeek, ' ', ''),'/','_'))+ '_'+ trim(replaceAll($BidderRouterHelper/CurrentRound, ' ', ''))+'.xlsx'`**
            2. **Create Variable **$BidDataDoc_Name** = `$BidDataDoc/Name`**
            3. **Create Variable **$BidDataDoc_FileID** = `toString($BidDataDoc/FileID)`**
            4. **Call Microflow **Custom_Logging.SUB_Log_Info****
            5. **JavaCallAction**
            6. **Commit/Save **$BidDataDoc** to Database**
            7. **DownloadFile**
            8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            9. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Error****
            2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.