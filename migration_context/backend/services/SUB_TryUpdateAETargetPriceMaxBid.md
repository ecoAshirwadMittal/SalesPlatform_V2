# Microflow Detailed Specification: SUB_TryUpdateAETargetPriceMaxBid

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)
- **$MaxLotBid** (Type: AuctionUI.MaxLotBid)
- **$RoundNumber** (Type: Variable)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$AggregatedInventoryList** (Type: AuctionUI.AggregatedInventory)
- **$PurchaseOrder** (Type: EcoATM_PO.PurchaseOrder)
- **$MinBidConfig** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.SUB_UpdateAETargetPriceMaxBid****
2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.