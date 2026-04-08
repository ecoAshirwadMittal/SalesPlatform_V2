# Microflow Detailed Specification: ACT_EditAuctionName

### 📥 Inputs (Parameters)
- **$AggInventoryHelper** (Type: AuctionUI.AggInventoryHelper)
- **$SchedulingAuction_Helper** (Type: AuctionUI.SchedulingAuction_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **AggInventoryHelper_Auction** via Association from **$AggInventoryHelper** (Result: **$Auction**)**
2. **Maps to Page: **AuctionUI.Edit_Auction_Name****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.