# Microflow Detailed Specification: ACT_OpenRound3BidUploadPage

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)
- **$CompanyName** (Type: Variable)
- **$RoundThreeBuyersDataReport** (Type: AuctionUI.RoundThreeBuyersDataReport)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[AuctionUI.SchedulingAuction_Auction = $Auction and Round = 3]` (Result: **$Round3SchedulingAuction**)**
3. **DB Retrieve **EcoATM_BuyerManagement.Buyer** Filter: `[ ( CompanyName = $CompanyName ) ]` (Result: **$Buyer**)**
4. **Maps to Page: **AuctionUI.PG_Round3_BidData_XMLUpload_BidRound****
5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.