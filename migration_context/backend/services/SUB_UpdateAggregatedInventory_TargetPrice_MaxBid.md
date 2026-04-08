# Microflow Detailed Specification: SUB_UpdateAggregatedInventory_TargetPrice_MaxBid

### 📥 Inputs (Parameters)
- **$IteratorMaxLotBid** (Type: AuctionUI.MaxLotBid)
- **$AggregatedInventory** (Type: AuctionUI.AggregatedInventory)
- **$NewTargetPrice** (Type: Variable)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$ReserveBid** (Type: Variable)
- **$TargetPriceFactor** (Type: AuctionUI.TargetPriceFactor)
- **$MaxBidDataList** (Type: AuctionUI.BidData)
- **$MaxBuyerCodeDisplay** (Type: Variable)
- **$PODetail** (Type: EcoATM_PO.PODetail)
- **$MinBidConfig** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$AggregatedInventory**
      - Set **AvgTargetPrice** = `$NewTargetPrice`
      - Set **DWAvgTargetPrice** = `$NewTargetPrice`**
2. 🔀 **DECISION:** `$SchedulingAuction/Round = 2`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$SchedulingAuction/Round = 3`
         ➔ **If [true]:**
            1. **Update **$AggregatedInventory**
      - Set **Round2MaxBid** = `$IteratorMaxLotBid/MaxBid`
      - Set **Round3TargetPrice** = `if $MinBidConfig > $NewTargetPrice then $MinBidConfig else $NewTargetPrice`
      - Set **Round3EBForTarget** = `$ReserveBid`
      - Set **R3TargetPriceFactor** = `if($TargetPriceFactor/FactorAmount != empty) then $TargetPriceFactor/FactorAmount else empty`
      - Set **R3TargetPriceFactorType** = `if($TargetPriceFactor/FactorType != empty) then $TargetPriceFactor/FactorType else empty`
      - Set **Round2MaxBidBuyerCode** = `$MaxBuyerCodeDisplay`
      - Set **R3POMaxBidWeek** = `$PODetail/EcoATM_PO.PODetail_PurchaseOrder/EcoATM_PO.PurchaseOrder/EcoATM_PO.PurchaseOrder_Week_From/EcoATM_MDM.Week`
      - Set **R3POMaxBid** = `$PODetail/Price`
      - Set **R3POMaxBuyerCode** = `$PODetail/EcoATM_PO.PODetail_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code`
      - Set **R3POMaxBidWeek** = `$PODetail/EcoATM_PO.PODetail_PurchaseOrder/EcoATM_PO.PurchaseOrder/EcoATM_PO.PurchaseOrder_Week_From/EcoATM_MDM.Week`**
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Update **$AggregatedInventory**
      - Set **Round1MaxBid** = `$IteratorMaxLotBid/MaxBid`
      - Set **Round2TargetPrice** = `if $MinBidConfig > $NewTargetPrice then $MinBidConfig else $NewTargetPrice`
      - Set **Round2EBForTarget** = `$ReserveBid`
      - Set **R2TargetPriceFactor** = `if($TargetPriceFactor/FactorAmount != empty) then $TargetPriceFactor/FactorAmount else empty`
      - Set **R2TargetPriceFactorType** = `if($TargetPriceFactor/FactorType != empty) then $TargetPriceFactor/FactorType else empty`
      - Set **Round1MaxBidBuyerCode** = `$MaxBuyerCodeDisplay`
      - Set **R2POMaxBid** = `$PODetail/Price`
      - Set **R2POMaxBidWeek** = `$PODetail/EcoATM_PO.PODetail_PurchaseOrder/EcoATM_PO.PurchaseOrder/EcoATM_PO.PurchaseOrder_Week_From/EcoATM_MDM.Week`
      - Set **R2POMaxBuyerCode** = `$PODetail/EcoATM_PO.PODetail_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.