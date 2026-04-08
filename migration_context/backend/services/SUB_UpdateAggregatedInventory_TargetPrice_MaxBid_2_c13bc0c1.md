# Microflow Analysis: SUB_UpdateAggregatedInventory_TargetPrice_MaxBid_2

### Requirements (Inputs):
- **$IteratorMaxLotBid** (A record of type: EcoATM_Inventory.MaxLotBid)
- **$AggregatedInventory** (A record of type: AuctionUI.AggregatedInventory)
- **$NewTargetPrice** (A record of type: Object)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Decision:** "DW?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggregatedInventory.DWAvgTargetPrice] to: "$NewTargetPrice
"
      - Change [AuctionUI.AggregatedInventory.AvgTargetPrice] to: "if $AggregatedInventory/TotalQuantity = 0 then $NewTargetPrice else 0
"**
3. **Decision:** "Round 2?"
   - If [false] -> Move to: **Round 3?**
   - If [true] -> Move to: **Activity**
4. **Decision:** "Round 3?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
5. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggregatedInventory.Round2MaxBid] to: "$IteratorMaxLotBid/MaxBid
"
      - Change [AuctionUI.AggregatedInventory.Round3TargetPrice_DW] to: "$NewTargetPrice
"
      - Change [AuctionUI.AggregatedInventory.Round3TargetPrice] to: "if $AggregatedInventory/TotalQuantity = 0 then $NewTargetPrice else 0
"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
