# Microflow Analysis: SUB_UpdateAETargetPriceMaxBid

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)
- **$MaxLotBid** (A record of type: AuctionUI.MaxLotBid)
- **$RoundNumber** (A record of type: Object)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$UpdatedAggregatedInventoryList** (A record of type: AuctionUI.AggregatedInventory)
- **$PurchaseOrder** (A record of type: EcoATM_PO.PurchaseOrder)
- **$MinBidConfig** (A record of type: Object)

### Execution Steps:
1. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [AuctionUI.AggregatedInventory_Week/EcoATM_MDM.Week/id = $Week]
[EcoId = $MaxLotBid/ProductId] 
[MergedGrade = $MaxLotBid/Grade]
 } (Call this list **$AggregatedInventory**)**
2. **Decision:** "Inventory exists?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Maxbid > 0**
3. **Run another process: "Custom_Logging.SUB_Log_Warning"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
