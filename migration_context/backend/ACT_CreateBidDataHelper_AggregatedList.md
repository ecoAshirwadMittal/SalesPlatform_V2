# Microflow Detailed Specification: ACT_CreateBidDataHelper_AggregatedList

### 📥 Inputs (Parameters)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_SchedulingAuction/AuctionUI.SchedulingAuction/Round=2`
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.ACT_Round2AggregatedInventory** (Result: **$AggregatedInventoryList**)**
      2. 🏁 **END:** Return `$AggregatedInventoryList`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
         ➔ **If [true]:**
            1. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ AuctionUI.AggregatedInventory_Week=$Week and DWTotalQuantity > 0 ]` (Result: **$AgregatedInventoryList_DataWipe**)**
            2. **LogMessage**
            3. 🏁 **END:** Return `$AgregatedInventoryList_DataWipe`
         ➔ **If [false]:**
            1. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[AuctionUI.AggregatedInventory_Week=$Week]` (Result: **$AgregatedInventoryList_All**)**
            2. **LogMessage**
            3. 🏁 **END:** Return `$AgregatedInventoryList_All`

**Final Result:** This process concludes by returning a [List] value.