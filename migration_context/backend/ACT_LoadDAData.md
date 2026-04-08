# Microflow Detailed Specification: ACT_LoadDAData

### 📥 Inputs (Parameters)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$DAHelper/EcoATM_DA.DAHelper_DAWeek!=empty`
   ➔ **If [true]:**
      1. **Retrieve related **DAHelper_DAWeek** via Association from **$DAHelper** (Result: **$DAWeek**)**
      2. **Retrieve related **DAWeek_Week** via Association from **$DAWeek** (Result: **$Week**)**
      3. **ExecuteDatabaseQuery**
      4. **List Operation: **Head** on **$undefined** (Result: **$NewDAUploadTime**)**
      5. **Create Variable **$Variable_3** = `parseDateTime($NewDAUploadTime/MAXUPLOADTIME, 'yyyy-MM-dd''T''HH:mm:ss')`**
      6. 🔀 **DECISION:** `$DAWeek/LastUploadTime!=empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `parseDateTime($NewDAUploadTime/MAXUPLOADTIME, 'yyyy-MM-dd''T''HH:mm:ss')> $DAWeek/LastUploadTime`
               ➔ **If [false]:**
                  1. **Update **$DAHelper**
      - Set **DisplayDA_DataGrid** = `true`**
                  2. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Update **$DAWeek** (and Save to DB)
      - Set **LastUploadTime** = `parseDateTime($NewDAUploadTime/MAXUPLOADTIME, 'yyyy-MM-dd''T''HH:mm:ss')`**
                  2. **Retrieve related **DeviceAllocation_DAWeek** via Association from **$DAWeek** (Result: **$DeviceAllocationList**)**
                  3. **Delete**
                  4. **ExecuteDatabaseQuery**
                  5. **ExportXml**
                  6. **ImportXml**
                  7. **List Operation: **Find** on **$undefined** where `'Total'` (Result: **$NewAggregateRevenueTotal**)**
                  8. **Call Microflow **EcoATM_DA.DS_GetOrCreateAggregates** (Result: **$AggregateRevenueTotal**)**
                  9. **Update **$AggregateRevenueTotal** (and Save to DB)
      - Set **TotalPayout** = `$NewAggregateRevenueTotal/Payout`
      - Set **TotalAvailableQty** = `$NewAggregateRevenueTotal/AvailableQty`
      - Set **TotalSold** = `$NewAggregateRevenueTotal/SalesQty`
      - Set **TotalRevenue** = `$NewAggregateRevenueTotal/Revenue`
      - Set **TotalMargin** = `$NewAggregateRevenueTotal/Margin`
      - Set **MarginPercentage** = `$NewAggregateRevenueTotal/MarginPercentage`
      - Set **AveragePurchasePrice** = `$NewAggregateRevenueTotal/Payout`**
                  10. **Delete**
                  11. **Update **$DAHelper**
      - Set **DisplayDA_DataGrid** = `true`**
                  12. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update **$DAWeek** (and Save to DB)
      - Set **LastUploadTime** = `parseDateTime($NewDAUploadTime/MAXUPLOADTIME, 'yyyy-MM-dd''T''HH:mm:ss')`**
            2. **Retrieve related **DeviceAllocation_DAWeek** via Association from **$DAWeek** (Result: **$DeviceAllocationList**)**
            3. **Delete**
            4. **ExecuteDatabaseQuery**
            5. **ExportXml**
            6. **ImportXml**
            7. **List Operation: **Find** on **$undefined** where `'Total'` (Result: **$NewAggregateRevenueTotal**)**
            8. **Call Microflow **EcoATM_DA.DS_GetOrCreateAggregates** (Result: **$AggregateRevenueTotal**)**
            9. **Update **$AggregateRevenueTotal** (and Save to DB)
      - Set **TotalPayout** = `$NewAggregateRevenueTotal/Payout`
      - Set **TotalAvailableQty** = `$NewAggregateRevenueTotal/AvailableQty`
      - Set **TotalSold** = `$NewAggregateRevenueTotal/SalesQty`
      - Set **TotalRevenue** = `$NewAggregateRevenueTotal/Revenue`
      - Set **TotalMargin** = `$NewAggregateRevenueTotal/Margin`
      - Set **MarginPercentage** = `$NewAggregateRevenueTotal/MarginPercentage`
      - Set **AveragePurchasePrice** = `$NewAggregateRevenueTotal/Payout`**
            10. **Delete**
            11. **Update **$DAHelper**
      - Set **DisplayDA_DataGrid** = `true`**
            12. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$DAHelper**
      - Set **DisplayDA_DataGrid** = `false`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.