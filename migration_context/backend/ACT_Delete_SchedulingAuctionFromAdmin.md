# Microflow Detailed Specification: ACT_Delete_SchedulingAuctionFromAdmin

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction**)**
2. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
3. **Delete**
4. **ExecuteDatabaseQuery**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.