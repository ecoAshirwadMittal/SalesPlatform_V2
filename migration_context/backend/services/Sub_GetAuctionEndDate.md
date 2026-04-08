# Microflow Detailed Specification: Sub_GetAuctionEndDate

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
2. **List Operation: **Sort** on **$undefined** sorted by: Round (Descending) (Result: **$SortedSchedulingAuctionList**)**
3. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/HasRound=true` (Result: **$LastSchedulingAuction**)**
4. 🏁 **END:** Return `$LastSchedulingAuction`

**Final Result:** This process concludes by returning a [Object] value.