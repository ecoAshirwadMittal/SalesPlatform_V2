# Microflow Detailed Specification: SUB_UpdateAETargetPriceMaxBid

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)
- **$MaxLotBid** (Type: AuctionUI.MaxLotBid)
- **$RoundNumber** (Type: Variable)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$UpdatedAggregatedInventoryList** (Type: AuctionUI.AggregatedInventory)
- **$PurchaseOrder** (Type: EcoATM_PO.PurchaseOrder)
- **$MinBidConfig** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week/EcoATM_MDM.Week/id = $Week] [EcoId = $MaxLotBid/ProductId] [MergedGrade = $MaxLotBid/Grade]` (Result: **$AggregatedInventory**)**
2. 🔀 **DECISION:** `$AggregatedInventory != empty`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$MaxLotBid/MaxBid > 0`
         ➔ **If [true]:**
            1. **DB Retrieve **AuctionUI.TargetPriceFactor** Filter: `[AuctionUI.TargetPriceFactor_BidRoundSelectionFilter/AuctionUI.BidRoundSelectionFilter/Round = $RoundNumber] [ ( MinimumValue <= $MaxLotBid/MaxBid and (MaximumValue = empty or MaximumValue >= $MaxLotBid/MaxBid) ) ]` (Result: **$TargetPriceFactor**)**
            2. **Create Variable **$MaxBidPlusFactor** = `0`**
            3. 🔀 **DECISION:** `$TargetPriceFactor != empty`
               ➔ **If [true]:**
                  1. **Update Variable **$MaxBidPlusFactor** = `if($TargetPriceFactor/FactorType != empty and $TargetPriceFactor/FactorType = AuctionUI.ENUM_TargetPriceFactorType.Percentage_Factor) then round($MaxLotBid/MaxBid * $TargetPriceFactor/FactorAmount div 100,2) else round($MaxLotBid/MaxBid + $TargetPriceFactor/FactorAmount,2)`**
                  2. **Retrieve related **BidData_AggregatedInventory** via Association from **$AggregatedInventory** (Result: **$BidDataList**)**
                  3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/BidAmount = $MaxLotBid/MaxBid` (Result: **$MaxBidDataList**)**
                  4. **List Operation: **Union** on **$undefined** (Result: **$BidDataList_distinct**)**
                  5. **AggregateList**
                  6. **Create Variable **$MaxBuyerCodeDisplay** = `''`**
                  7. 🔄 **LOOP:** For each **$IteratorBidData** in **$MaxBidDataList**
                     │ 1. 🔀 **DECISION:** `$MaxBuyerCodeDisplay = ''`
                     │    ➔ **If [true]:**
                     │       1. **Update Variable **$MaxBuyerCodeDisplay** = `$IteratorBidData/AuctionUI.BidData_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code`**
                     │    ➔ **If [false]:**
                     │       1. **Update Variable **$MaxBuyerCodeDisplay** = `$MaxBuyerCodeDisplay+','+$IteratorBidData/Code`**
                     └─ **End Loop**
                  8. **DB Retrieve **EcoATM_EB.ReserveBid** Filter: `[ProductId = $MaxLotBid/ProductId] [Grade = $MaxLotBid/Grade]` (Result: **$ReserveBid**)**
                  9. **Create Variable **$ReserveBidValue** = `if $ReserveBid != empty then $ReserveBid/Bid else 0`**
                  10. **DB Retrieve **EcoATM_PO.PODetail** Filter: `[EcoATM_PO.PODetail_PurchaseOrder/EcoATM_PO.PurchaseOrder = $PurchaseOrder] [ProductID = $MaxLotBid/ProductId] [Grade = $MaxLotBid/Grade]` (Result: **$MaxPODetail**)**
                  11. **Create Variable **$EvaluatedBid** = `if $ReserveBid != empty then max($MaxBidPlusFactor, $ReserveBid/Bid) else $MaxBidPlusFactor`**
                  12. **Update Variable **$EvaluatedBid** = `if $MaxPODetail != empty then max($EvaluatedBid, $MaxPODetail/Price) else $EvaluatedBid`**
                  13. **Call Microflow **AuctionUI.SUB_UpdateAggregatedInventory_TargetPrice_MaxBid****
                  14. **Add **$$AggregatedInventory
** to/from list **$UpdatedAggregatedInventoryList****
                  15. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
                  2. **Update Variable **$MaxBidPlusFactor** = `$MaxLotBid/MaxBid`**
                  3. **Retrieve related **BidData_AggregatedInventory** via Association from **$AggregatedInventory** (Result: **$BidDataList**)**
                  4. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/BidAmount = $MaxLotBid/MaxBid` (Result: **$MaxBidDataList**)**
                  5. **List Operation: **Union** on **$undefined** (Result: **$BidDataList_distinct**)**
                  6. **AggregateList**
                  7. **Create Variable **$MaxBuyerCodeDisplay** = `''`**
                  8. 🔄 **LOOP:** For each **$IteratorBidData** in **$MaxBidDataList**
                     │ 1. 🔀 **DECISION:** `$MaxBuyerCodeDisplay = ''`
                     │    ➔ **If [true]:**
                     │       1. **Update Variable **$MaxBuyerCodeDisplay** = `$IteratorBidData/AuctionUI.BidData_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code`**
                     │    ➔ **If [false]:**
                     │       1. **Update Variable **$MaxBuyerCodeDisplay** = `$MaxBuyerCodeDisplay+','+$IteratorBidData/Code`**
                     └─ **End Loop**
                  9. **DB Retrieve **EcoATM_EB.ReserveBid** Filter: `[ProductId = $MaxLotBid/ProductId] [Grade = $MaxLotBid/Grade]` (Result: **$ReserveBid**)**
                  10. **Create Variable **$ReserveBidValue** = `if $ReserveBid != empty then $ReserveBid/Bid else 0`**
                  11. **DB Retrieve **EcoATM_PO.PODetail** Filter: `[EcoATM_PO.PODetail_PurchaseOrder/EcoATM_PO.PurchaseOrder = $PurchaseOrder] [ProductID = $MaxLotBid/ProductId] [Grade = $MaxLotBid/Grade]` (Result: **$MaxPODetail**)**
                  12. **Create Variable **$EvaluatedBid** = `if $ReserveBid != empty then max($MaxBidPlusFactor, $ReserveBid/Bid) else $MaxBidPlusFactor`**
                  13. **Update Variable **$EvaluatedBid** = `if $MaxPODetail != empty then max($EvaluatedBid, $MaxPODetail/Price) else $EvaluatedBid`**
                  14. **Call Microflow **AuctionUI.SUB_UpdateAggregatedInventory_TargetPrice_MaxBid****
                  15. **Add **$$AggregatedInventory
** to/from list **$UpdatedAggregatedInventoryList****
                  16. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
            2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.