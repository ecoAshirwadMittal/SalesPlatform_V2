# Microflow Analysis: SUB_TryUpdateAETargetPriceMaxBid

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)
- **$MaxLotBid** (A record of type: AuctionUI.MaxLotBid)
- **$RoundNumber** (A record of type: Object)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$AggregatedInventoryList** (A record of type: AuctionUI.AggregatedInventory)
- **$PurchaseOrder** (A record of type: EcoATM_PO.PurchaseOrder)
- **$MinBidConfig** (A record of type: Object)

### Execution Steps:
1. **Run another process: "AuctionUI.SUB_UpdateAETargetPriceMaxBid"** ⚠️ *(This step has a safety catch if it fails)*
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
