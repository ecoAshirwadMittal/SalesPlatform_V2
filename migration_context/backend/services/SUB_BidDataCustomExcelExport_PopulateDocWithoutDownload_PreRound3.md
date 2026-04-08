# Microflow Detailed Specification: SUB_BidDataCustomExcelExport_PopulateDocWithoutDownload_PreRound3

### 📥 Inputs (Parameters)
- **$Template** (Type: XLSReport.MxTemplate)
- **$BidDataDoc** (Type: AuctionUI.BidDataDoc)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. 🔀 **DECISION:** `trim($BidDataDoc/Name) != ''`
   ➔ **If [true]:**
      1. **Create Variable **$FileName** = `$BuyerCode/Code+'_'+ trim(replaceAll(replaceAll($SchedulingAuction/Auction_Week_Year, ' ', ''),'/','_'))+ '_'+ trim(replaceAll(toString($SchedulingAuction/Round), ' ', ''))+'.xlsx'`**
      2. **Create Variable **$BidDataDoc_Name** = `$BidDataDoc/Name`**
      3. **Create Variable **$BidDataDoc_FileID** = `toString($BidDataDoc/FileID)`**
      4. **Call Microflow **Custom_Logging.SUB_Log_Info****
      5. **JavaCallAction**
      6. **Commit/Save **$BidDataDoc** to Database**
      7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      8. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$BidDataDoc**
      - Set **Name** = `'Missing Name'`**
      2. **Create Variable **$FileName** = `$BuyerCode/Code+'_'+ trim(replaceAll(replaceAll($SchedulingAuction/Auction_Week_Year, ' ', ''),'/','_'))+ '_'+ trim(replaceAll(toString($SchedulingAuction/Round), ' ', ''))+'.xlsx'`**
      3. **Create Variable **$BidDataDoc_Name** = `$BidDataDoc/Name`**
      4. **Create Variable **$BidDataDoc_FileID** = `toString($BidDataDoc/FileID)`**
      5. **Call Microflow **Custom_Logging.SUB_Log_Info****
      6. **JavaCallAction**
      7. **Commit/Save **$BidDataDoc** to Database**
      8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.