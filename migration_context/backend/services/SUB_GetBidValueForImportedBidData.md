# Microflow Detailed Specification: SUB_GetBidValueForImportedBidData

### 📥 Inputs (Parameters)
- **$BidDataImport_Round3** (Type: AuctionUI.BidDataImport_Round3)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Create Variable **$Accept_Max_Value** = `replaceAll(trim($BidDataImport_Round3/Accept_Max_Bid_YN),'\$','')`**
3. 🔀 **DECISION:** `$Accept_Max_Value = 'Y' or $Accept_Max_Value ='y'`
   ➔ **If [true]:**
      1. **Create Variable **$parsedMaxBid** = `parseDecimal(replaceAll(trim($BidDataImport_Round3/Max_Bid),'\$',''))`**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return `$parsedMaxBid`
   ➔ **If [false]:**
      1. **Create Variable **$parsedAcceptMax** = `parseDecimal($BidDataImport_Round3/Accept_Max_Bid_YN)`**
      2. 🏁 **END:** Return `$parsedAcceptMax`

**Final Result:** This process concludes by returning a [Decimal] value.