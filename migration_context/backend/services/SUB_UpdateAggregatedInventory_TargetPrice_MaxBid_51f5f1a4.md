# Microflow Analysis: SUB_UpdateAggregatedInventory_TargetPrice_MaxBid

### Requirements (Inputs):
- **$IteratorMaxLotBid** (A record of type: AuctionUI.MaxLotBid)
- **$AggregatedInventory** (A record of type: AuctionUI.AggregatedInventory)
- **$NewTargetPrice** (A record of type: Object)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$ReserveBid** (A record of type: Object)
- **$TargetPriceFactor** (A record of type: AuctionUI.TargetPriceFactor)
- **$MaxBidDataList** (A record of type: AuctionUI.BidData)
- **$MaxBuyerCodeDisplay** (A record of type: Object)
- **$PODetail** (A record of type: EcoATM_PO.PODetail)
- **$MinBidConfig** (A record of type: Object)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggregatedInventory.AvgTargetPrice] to: "$NewTargetPrice
"
      - Change [AuctionUI.AggregatedInventory.DWAvgTargetPrice] to: "$NewTargetPrice"**
2. **Decision:** "Round 2?"
   - If [false] -> Move to: **Round 3?**
   - If [true] -> Move to: **Activity**
3. **Decision:** "Round 3?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggregatedInventory.Round2MaxBid] to: "$IteratorMaxLotBid/MaxBid
"
      - Change [AuctionUI.AggregatedInventory.Round3TargetPrice] to: "if $MinBidConfig > $NewTargetPrice
then $MinBidConfig
else $NewTargetPrice"
      - Change [AuctionUI.AggregatedInventory.Round3EBForTarget] to: "$ReserveBid"
      - Change [AuctionUI.AggregatedInventory.R3TargetPriceFactor] to: "if($TargetPriceFactor/FactorAmount != empty)
then $TargetPriceFactor/FactorAmount
else empty
"
      - Change [AuctionUI.AggregatedInventory.R3TargetPriceFactorType] to: "if($TargetPriceFactor/FactorType != empty)
then $TargetPriceFactor/FactorType
else empty
"
      - Change [AuctionUI.AggregatedInventory.Round2MaxBidBuyerCode] to: "$MaxBuyerCodeDisplay"
      - Change [AuctionUI.R3POMaxBidWeek] to: "$PODetail/EcoATM_PO.PODetail_PurchaseOrder/EcoATM_PO.PurchaseOrder/EcoATM_PO.PurchaseOrder_Week_From/EcoATM_MDM.Week
"
      - Change [AuctionUI.AggregatedInventory.R3POMaxBid] to: "$PODetail/Price"
      - Change [AuctionUI.AggregatedInventory.R3POMaxBuyerCode] to: "$PODetail/EcoATM_PO.PODetail_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code"
      - Change [AuctionUI.R3POMaxBidWeek] to: "$PODetail/EcoATM_PO.PODetail_PurchaseOrder/EcoATM_PO.PurchaseOrder/EcoATM_PO.PurchaseOrder_Week_From/EcoATM_MDM.Week"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
