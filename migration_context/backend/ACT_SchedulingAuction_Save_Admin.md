# Microflow Detailed Specification: ACT_SchedulingAuction_Save_Admin

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Commit/Save **$SchedulingAuction** to Database**
2. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction**)**
3. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
4. **ExecuteDatabaseQuery**
5. **Close current page/popup**
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.