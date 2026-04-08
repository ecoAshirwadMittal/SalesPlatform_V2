# Microflow Detailed Specification: ACT_DownloadRound3ValidBidsForBuyer

### 📥 Inputs (Parameters)
- **$RoundThreeBuyersData** (Type: AuctionUI.RoundThreeBuyersDataReport)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[AuctionUI.SchedulingAuction_Auction = $Auction and Round = 3]` (Result: **$Round3SchedulingAuction**)**
3. **DB Retrieve **EcoATM_BuyerManagement.Buyer** Filter: `[ ( CompanyName = $RoundThreeBuyersData/CompanyName ) ]` (Result: **$Buyer**)**
4. **Call Microflow **AuctionUI.ACT_DeleteRound3BidDataForBuyer****
5. **Call Microflow **AuctionUI.DS_Round3ValidBidsForBuyer** (Result: **$RoundThreeBidDataReportList**)**
6. 🔀 **DECISION:** `$RoundThreeBidDataReportList!=empty`
   ➔ **If [true]:**
      1. **Create **AuctionUI.RoundThreeBidDataExcelExport** (Result: **$NewRoundThreeBidDataExcelExport**)
      - Set **RoundThreeBidDataExcelExport_RoundThreeBidDataReport** = `$RoundThreeBidDataReportList`
      - Set **DeleteAfterDownload** = `true`**
      2. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='RoundThreeBiddataExport']` (Result: **$MxTemplate**)**
      3. **Create Variable **$FileName** = `$Auction/AuctionUI.Auction_Week/EcoATM_MDM.Week/Year +' W'+$Auction/AuctionUI.Auction_Week/EcoATM_MDM.Week/WeekNumber+' Closing Offer - '+$RoundThreeBuyersData/CompanyName+'.xlsx'`**
      4. **JavaCallAction**
      5. **Update **$ExcelExport**
      - Set **Name** = `$FileName`**
      6. **DownloadFile**
      7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      8. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Warning): `No Bid Data exists for Buyer`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.