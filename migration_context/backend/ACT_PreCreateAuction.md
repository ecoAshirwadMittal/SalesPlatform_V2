# Microflow Detailed Specification: ACT_PreCreateAuction

### 📥 Inputs (Parameters)
- **$AggInventoryHelper** (Type: AuctionUI.AggInventoryHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.GetOrCreateSchedulingAuctionHelper** (Result: **$SchedulingAuction_Helper**)**
2. **Maps to Page: **AuctionUI.Create_Auction****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.