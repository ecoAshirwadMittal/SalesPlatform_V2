# Microflow Detailed Specification: ACT_LoadDAData_BKP

### 📥 Inputs (Parameters)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$DAHelper/EcoATM_DA.DAHelper_DAWeek!=empty`
   ➔ **If [true]:**
      1. **Retrieve related **DAHelper_DAWeek** via Association from **$DAHelper** (Result: **$DAWeek**)**
      2. **ExecuteDatabaseQuery**
      3. **List Operation: **Head** on **$undefined** (Result: **$NewDAUploadTime**)**
      4. **Create Variable **$Variable_3** = `parseDateTime($NewDAUploadTime/MAXUPLOADTIME, 'yyyy-MM-dd''T''HH:mm:ss')`**
      5. 🔀 **DECISION:** `$DAWeek/LastUploadTime!=empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `parseDateTime($NewDAUploadTime/MAXUPLOADTIME, 'yyyy-MM-dd''T''HH:mm:ss')> $DAWeek/LastUploadTime`
               ➔ **If [false]:**
                  1. **Update **$DAHelper**
      - Set **DisplayDA_DataGrid** = `true`**
                  2. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Update **$DAWeek** (and Save to DB)
      - Set **LastUploadTime** = `parseDateTime($NewDAUploadTime/MAXUPLOADTIME, 'yyyy-MM-dd''T''HH:mm:ss')`**
                  2. **ExecuteDatabaseQuery**
                  3. **List Operation: **Head** on **$undefined** (Result: **$NewAggregateRevenueTotal**)**
                  4. **Call Microflow **EcoATM_DA.DS_GetOrCreateAggregates** (Result: **$AggregateRevenueTotal**)**
                  5. **Update **$AggregateRevenueTotal** (and Save to DB)
      - Set **TotalPayout** = `$NewAggregateRevenueTotal/TOTALPAYOUT`
      - Set **TotalAvailableQty** = `$NewAggregateRevenueTotal/TOTALAVAILABLEQTY`
      - Set **TotalSold** = `$NewAggregateRevenueTotal/TOTALSOLD`
      - Set **TotalRevenue** = `$NewAggregateRevenueTotal/TOTALSOLD`
      - Set **TotalMargin** = `$NewAggregateRevenueTotal/TOTALMARGIN`
      - Set **MarginPercentage** = `$NewAggregateRevenueTotal/MARGINPERCENTAGE`
      - Set **AveragePurchasePrice** = `$NewAggregateRevenueTotal/AVERAGEPURCHASEPRICE`**
                  6. **ExecuteDatabaseQuery**
                  7. **ExportXml**
                  8. **ImportXml**
                  9. **ExecuteDatabaseQuery**
                  10. **ExportXml**
                  11. **ImportXml**
                  12. **Update **$DAHelper**
      - Set **DisplayDA_DataGrid** = `true`**
                  13. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update **$DAWeek** (and Save to DB)
      - Set **LastUploadTime** = `parseDateTime($NewDAUploadTime/MAXUPLOADTIME, 'yyyy-MM-dd''T''HH:mm:ss')`**
            2. **ExecuteDatabaseQuery**
            3. **List Operation: **Head** on **$undefined** (Result: **$NewAggregateRevenueTotal**)**
            4. **Call Microflow **EcoATM_DA.DS_GetOrCreateAggregates** (Result: **$AggregateRevenueTotal**)**
            5. **Update **$AggregateRevenueTotal** (and Save to DB)
      - Set **TotalPayout** = `$NewAggregateRevenueTotal/TOTALPAYOUT`
      - Set **TotalAvailableQty** = `$NewAggregateRevenueTotal/TOTALAVAILABLEQTY`
      - Set **TotalSold** = `$NewAggregateRevenueTotal/TOTALSOLD`
      - Set **TotalRevenue** = `$NewAggregateRevenueTotal/TOTALSOLD`
      - Set **TotalMargin** = `$NewAggregateRevenueTotal/TOTALMARGIN`
      - Set **MarginPercentage** = `$NewAggregateRevenueTotal/MARGINPERCENTAGE`
      - Set **AveragePurchasePrice** = `$NewAggregateRevenueTotal/AVERAGEPURCHASEPRICE`**
            6. **ExecuteDatabaseQuery**
            7. **ExportXml**
            8. **ImportXml**
            9. **ExecuteDatabaseQuery**
            10. **ExportXml**
            11. **ImportXml**
            12. **Update **$DAHelper**
      - Set **DisplayDA_DataGrid** = `true`**
            13. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$DAHelper**
      - Set **DisplayDA_DataGrid** = `false`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.