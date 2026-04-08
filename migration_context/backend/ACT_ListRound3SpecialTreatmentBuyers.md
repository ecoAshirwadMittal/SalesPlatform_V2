# Microflow Detailed Specification: ACT_ListRound3SpecialTreatmentBuyers

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
2. **List Operation: **Filter** on **$undefined** where `3` (Result: **$SchedulingAuctionFiltered**)**
3. **List Operation: **Head** on **$undefined** (Result: **$SchedulingAuctionRound3**)**
4. **DB Retrieve **EcoATM_BuyerManagement.Buyer** Filter: `[ ( isSpecialBuyer = true() and Status = 'Active' ) ]` (Result: **$BuyerList_IsSpecialTreatment**)**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.