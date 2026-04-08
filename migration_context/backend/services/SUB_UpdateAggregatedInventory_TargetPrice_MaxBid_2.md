# Microflow Detailed Specification: SUB_UpdateAggregatedInventory_TargetPrice_MaxBid_2

### 📥 Inputs (Parameters)
- **$IteratorMaxLotBid** (Type: EcoATM_Inventory.MaxLotBid)
- **$AggregatedInventory** (Type: AuctionUI.AggregatedInventory)
- **$NewTargetPrice** (Type: Variable)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$IteratorMaxLotBid/BuyerCodeType = 'Data_Wipe'`
   ➔ **If [true]:**
      1. **Update **$AggregatedInventory**
      - Set **DWAvgTargetPrice** = `$NewTargetPrice`
      - Set **AvgTargetPrice** = `if $AggregatedInventory/TotalQuantity = 0 then $NewTargetPrice else 0`**
      2. 🔀 **DECISION:** `$SchedulingAuction/Round = 2`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$SchedulingAuction/Round = 3`
               ➔ **If [true]:**
                  1. **Update **$AggregatedInventory**
      - Set **Round2MaxBid** = `$IteratorMaxLotBid/MaxBid`
      - Set **Round3TargetPrice_DW** = `$NewTargetPrice`
      - Set **Round3TargetPrice** = `if $AggregatedInventory/TotalQuantity = 0 then $NewTargetPrice else 0`**
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Update **$AggregatedInventory**
      - Set **Round1MaxBid** = `$IteratorMaxLotBid/MaxBid`
      - Set **Round2TargetPrice_DW** = `$NewTargetPrice`
      - Set **Round2TargetPrice** = `if $AggregatedInventory/TotalQuantity = 0 then $NewTargetPrice else 0`**
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$AggregatedInventory**
      - Set **AvgTargetPrice** = `$NewTargetPrice`**
      2. 🔀 **DECISION:** `$SchedulingAuction/Round = 2`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$SchedulingAuction/Round = 3`
               ➔ **If [true]:**
                  1. **Update **$AggregatedInventory**
      - Set **Round2MaxBid** = `$IteratorMaxLotBid/MaxBid`
      - Set **Round3TargetPrice** = `$NewTargetPrice`**
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Update **$AggregatedInventory**
      - Set **Round1MaxBid** = `$IteratorMaxLotBid/MaxBid`
      - Set **Round2TargetPrice** = `$NewTargetPrice`**
            2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.