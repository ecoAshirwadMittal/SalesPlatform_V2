# Microflow Detailed Specification: SUB_LoadAggregatedInventoryTotals

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. 🔀 **DECISION:** `$Week/AuctionUI.Auction_Week/AuctionUI.Auction=empty`
   ➔ **If [true]:**
      1. **ExecuteDatabaseQuery**
      2. **List Operation: **Head** on **$undefined** (Result: **$NewMaxUploadTimeAggreegatedInventory**)**
      3. 🔀 **DECISION:** `$NewMaxUploadTimeAggreegatedInventory !=empty and $NewMaxUploadTimeAggreegatedInventory/MAXUPLOADTIME !=empty`
         ➔ **If [true]:**
            1. **Create Variable **$MaxDateTime** = `parseDateTimeUTC ($NewMaxUploadTimeAggreegatedInventory/MAXUPLOADTIME , 'yyyy-MM-dd''T''HH:mm:ss')`**
            2. **Retrieve related **AggInventoryTotals_Week** via Association from **$Week** (Result: **$AggreegatedInventoryTotal_2**)**
            3. 🔀 **DECISION:** `1=0`
               ➔ **If [true]:**
                  1. **Call Microflow **AuctionUI.SUB_LoadAggregatedInventory****
                  2. **Call Microflow **AuctionUI.SUB_BuildAggregatedInventoryFilters****
                  3. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[DeviceId = 'Total'] [AuctionUI.AggregatedInventory_Week = $Week]` (Result: **$AggreegatedInventoryTotal**)**
                  4. **Call Microflow **AuctionUI.SUB_GetOrCreateAgrregateTotals** (Result: **$AggreegatedInventoryTotals**)**
                  5. **Update **$AggreegatedInventoryTotals** (and Save to DB)
      - Set **MaxUploadTime** = `$MaxDateTime`**
                  6. **Delete**
                  7. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$AggreegatedInventoryTotal_2=empty`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$AggreegatedInventoryTotal_2/MaxUploadTime!= $MaxDateTime`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Call Microflow **AuctionUI.SUB_LoadAggregatedInventory****
                              2. **Call Microflow **AuctionUI.SUB_BuildAggregatedInventoryFilters****
                              3. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[DeviceId = 'Total'] [AuctionUI.AggregatedInventory_Week = $Week]` (Result: **$AggreegatedInventoryTotal**)**
                              4. **Call Microflow **AuctionUI.SUB_GetOrCreateAgrregateTotals** (Result: **$AggreegatedInventoryTotals**)**
                              5. **Update **$AggreegatedInventoryTotals** (and Save to DB)
      - Set **MaxUploadTime** = `$MaxDateTime`**
                              6. **Delete**
                              7. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Call Microflow **AuctionUI.SUB_LoadAggregatedInventory****
                        2. **Call Microflow **AuctionUI.SUB_BuildAggregatedInventoryFilters****
                        3. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[DeviceId = 'Total'] [AuctionUI.AggregatedInventory_Week = $Week]` (Result: **$AggreegatedInventoryTotal**)**
                        4. **Call Microflow **AuctionUI.SUB_GetOrCreateAgrregateTotals** (Result: **$AggreegatedInventoryTotals**)**
                        5. **Update **$AggreegatedInventoryTotals** (and Save to DB)
      - Set **MaxUploadTime** = `$MaxDateTime`**
                        6. **Delete**
                        7. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.