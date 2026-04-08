# Microflow Detailed Specification: SUB_BidDownloadHelper_DWList_Depricated_10_28_24

### 📥 Inputs (Parameters)
- **$AggregatedInventoryList** (Type: EcoATM_Inventory.AggregatedInventory)
- **$BidDownload_HelperList** (Type: AuctionUI.BidDownload_Helper)
- **$NewBidDataDoc** (Type: AuctionUI.BidDataDoc)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_Caching.NP_BuyerCodeSelect_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AggregatedInventoryList**
   │ 1. **DB Retrieve **ECOATM_Buyer.BidData** Filter: `[Code = $NP_BuyerCodeSelect_Helper/Code] [ECOATM_Buyer.BidData_BidRound = $BidRound] [ EcoID = $IteratorAgregatedInventory_DW/ecoID and Merged_Grade = $IteratorAgregatedInventory_DW/Merged_Grade ]` (Result: **$ExistingBidData**)**
   │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
   │    ➔ **If [true]:**
   │       1. **Create **AuctionUI.BidDownload_Helper** (Result: **$UpdateBidDownload_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/ecoID`
      - Set **BidQuantity** = `if $ExistingBidData/BidQuantity = empty or $ExistingBidData/BidQuantity<0 then empty else $ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/Data_Wipe_Target_Price`
      - Set **MergedGrade** = `$IteratorAgregatedInventory_DW/Merged_Grade`
      - Set **BidDownload_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/Data_Wipe_Quantity`
      - Set **BidDownload_Helper_BidDataDoc** = `$NewBidDataDoc`
      - Set **Added** = `$IteratorAgregatedInventory_DW/Added`**
   │       2. **Add **$$UpdateBidDownload_Helper_DW** to/from list **$BidDownload_HelperList****
   │    ➔ **If [false]:**
   │       1. **Create **AuctionUI.BidDownload_Helper** (Result: **$NewBidDownload_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/ecoID`
      - Set **BidDownload_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `empty`
      - Set **MergedGrade** = `$IteratorAgregatedInventory_DW/Merged_Grade`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/Data_Wipe_Target_Price`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/Data_Wipe_Quantity`
      - Set **BidDownload_Helper_BidDataDoc** = `$NewBidDataDoc`
      - Set **Added** = `$IteratorAgregatedInventory_DW/Added`**
   │       2. **Add **$$NewBidDownload_Helper_DW** to/from list **$BidDownload_HelperList****
   └─ **End Loop**
3. **LogMessage**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.