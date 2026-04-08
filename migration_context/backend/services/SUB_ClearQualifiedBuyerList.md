# Microflow Detailed Specification: SUB_ClearQualifiedBuyerList

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'ClearQualifiedBuyers'`**
2. **Create Variable **$Description** = `'Clear Qualified Buyers: Auction-' + $SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionTitle+ ' , Round-' + $SchedulingAuction/Round`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **Retrieve related **QualifiedBuyerCodes_SchedulingAuction** via Association from **$SchedulingAuction** (Result: **$QualifiedBuyerCodeList**)**
5. **Delete**
6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.