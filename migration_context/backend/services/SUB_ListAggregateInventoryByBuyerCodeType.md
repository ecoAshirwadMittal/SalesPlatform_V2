# Microflow Detailed Specification: SUB_ListAggregateInventoryByBuyerCodeType

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
2. 🔀 **DECISION:** `$BuyerCode/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe`
   ➔ **If [true]:**
      1. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ AuctionUI.AggregatedInventory_Week = $Auction/AuctionUI.Auction_Week and DWTotalQuantity > 0 ]` (Result: **$AggregatedInventoryList_DataWipe**)**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
      3. 🏁 **END:** Return `$AggregatedInventoryList_DataWipe`
   ➔ **If [false]:**
      1. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week/EcoATM_MDM.Week/id= $Auction/AuctionUI.Auction_Week]` (Result: **$AggregatedInventoryList_NonDataWipe**)**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
      3. 🏁 **END:** Return `$AggregatedInventoryList_NonDataWipe`

**Final Result:** This process concludes by returning a [List] value.