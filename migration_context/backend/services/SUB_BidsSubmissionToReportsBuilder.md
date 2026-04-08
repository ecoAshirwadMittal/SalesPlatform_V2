# Microflow Detailed Specification: SUB_BidsSubmissionToReportsBuilder

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **Create Variable **$StartTime** = `[%CurrentDateTime%]`**
3. **Retrieve related **BidData_BidRound** via Association from **$BidRound** (Result: **$BidDataList**)**
4. **Call Microflow **AuctionUI.Sub_BidDataSanitize** (Result: **$BidDataList_Sanitized**)**
5. **Call Microflow **AuctionUI.SUB_BuildSummaryReportObject** (Result: **$BuyerBidSummaryReport**)**
6. **Call Microflow **AuctionUI.SUB_BidsSubmissionToBuyerSummaryReportBuilder****
7. **Create Variable **$EndTime** = `[%CurrentDateTime%]`**
8. **LogMessage**
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.