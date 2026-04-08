# Microflow Detailed Specification: SUB_GetBidQuantityValueForImportedBidData

### 📥 Inputs (Parameters)
- **$BidDataImport_Round3** (Type: AuctionUI.BidDataImport_Round3)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Create Variable **$Your_Quantity_Cap** = `trim($BidDataImport_Round3/Your_Quantity_Cap)`**
3. 🔀 **DECISION:** `$Your_Quantity_Cap = 'NO QUANTITY LIMIT'`
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return `empty`
   ➔ **If [false]:**
      1. **Create Variable **$parsedQtyCap** = `parseInteger($BidDataImport_Round3/Your_Quantity_Cap)`**
      2. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      3. 🏁 **END:** Return `$parsedQtyCap`

**Final Result:** This process concludes by returning a [Integer] value.