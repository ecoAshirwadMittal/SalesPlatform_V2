# Microflow Detailed Specification: ACT_SchedulingAuction_SendAllBidsToSnowflake_Admin

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction**)**
2. **Call Microflow **AuctionUI.ACT_Auction_SendAllBidsToSnowflake_Admin****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.