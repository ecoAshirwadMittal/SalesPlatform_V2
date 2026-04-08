# Microflow Detailed Specification: ACT_DeleteBidDataByBuyer

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Retrieve related **BidData_BidRound** via Association from **$BidRound** (Result: **$BidDataList**)**
3. **Delete**
4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
5. **Delete**
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.