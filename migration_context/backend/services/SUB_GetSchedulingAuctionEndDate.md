# Microflow Detailed Specification: SUB_GetSchedulingAuctionEndDate

### 📥 Inputs (Parameters)
- **$DAWeek** (Type: EcoATM_DA.DAWeek)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **DAWeek_Week** via Association from **$DAWeek** (Result: **$Week**)**
2. **Retrieve related **Auction_Week** via Association from **$Week** (Result: **$Auction**)**
3. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[HasRound] [AuctionUI.SchedulingAuction_Auction=$Auction]` (Result: **$SchedulingAuction**)**
4. 🏁 **END:** Return `$SchedulingAuction/End_DateTime`

**Final Result:** This process concludes by returning a [DateTime] value.