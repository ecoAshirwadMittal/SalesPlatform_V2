# Microflow Detailed Specification: SUB_BidDownloadHelper_CreateList_Depricated_10_28_24

### 📥 Inputs (Parameters)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_Caching.NP_BuyerCodeSelect_Helper)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$Week** (Type: EcoATM_Inventory.Week)
- **$AggregatedInventoryList** (Type: EcoATM_Inventory.AggregatedInventory)
- **$NewBidDataDoc** (Type: AuctionUI.BidDataDoc)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **CreateList**
3. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
   ➔ **If [true]:**
      1. **Call Microflow **ECOATM_Buyer.SUB_BidDownloadHelper_DWList****
      2. **LogMessage**
      3. 🏁 **END:** Return `$BidDownload_HelperList`
   ➔ **If [false]:**
      1. **Call Microflow **ECOATM_Buyer.SUB_BidDownloadHelper_NonDWList****
      2. **LogMessage**
      3. 🏁 **END:** Return `$BidDownload_HelperList`

**Final Result:** This process concludes by returning a [List] value.