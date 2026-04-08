# Microflow Detailed Specification: SUB_BidDownloadHelper_NonDWList_Depricated_10_28_24

### 📥 Inputs (Parameters)
- **$AggregatedInventoryList** (Type: EcoATM_Inventory.AggregatedInventory)
- **$BidDownload_HelperList** (Type: AuctionUI.BidDownload_Helper)
- **$NewBidDataDoc** (Type: AuctionUI.BidDataDoc)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_Caching.NP_BuyerCodeSelect_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AggregatedInventoryList**
   │ 1. **DB Retrieve **ECOATM_Buyer.BidData** Filter: `[Code = $NP_BuyerCodeSelect_Helper/Code] [ECOATM_Buyer.BidData_BidRound = $BidRound] [ EcoID = $IteratorAgregatedInventory/ecoID and Merged_Grade = $IteratorAgregatedInventory/Merged_Grade ]` (Result: **$ExistingBidData**)**
   │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
   │    ➔ **If [true]:**
   │       1. **Create **AuctionUI.BidDownload_Helper** (Result: **$UpdateBidDownload_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/ecoID`
      - Set **BidQuantity** = `if $ExistingBidData/BidQuantity = empty or $ExistingBidData/BidQuantity<0 then empty else $ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/TargetPrice`
      - Set **MergedGrade** = `$IteratorAgregatedInventory/Merged_Grade`
      - Set **BidDownload_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/Total_Quantity`
      - Set **BidDownload_Helper_BidDataDoc** = `$NewBidDataDoc`
      - Set **Added** = `$IteratorAgregatedInventory/Added`**
   │       2. **Add **$$UpdateBidDownload_Helper** to/from list **$BidDownload_HelperList****
   │    ➔ **If [false]:**
   │       1. **Create **AuctionUI.BidDownload_Helper** (Result: **$NewBidDownload_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/ecoID`
      - Set **BidDownload_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `empty`
      - Set **MergedGrade** = `$IteratorAgregatedInventory/Merged_Grade`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/TargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/Total_Quantity`
      - Set **BidDownload_Helper_BidDataDoc** = `$NewBidDataDoc`
      - Set **Added** = `$IteratorAgregatedInventory/Added`**
   │       2. **Add **$$NewBidDownload_Helper** to/from list **$BidDownload_HelperList****
   └─ **End Loop**
3. **LogMessage**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.