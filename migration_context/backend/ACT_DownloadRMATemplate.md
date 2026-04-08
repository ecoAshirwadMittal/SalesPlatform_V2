# Microflow Detailed Specification: ACT_DownloadRMATemplate

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_RMA.RMATemplate** Filter: `[IsActive]` (Result: **$RMATemplate**)**
2. 🔀 **DECISION:** `$RMATemplate != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$RMATemplate/Name = 'RMA_Request_' + formatDateTime([%BeginOfCurrentDay%], 'yyyyMMdd') + '.xlsx'`
         ➔ **If [true]:**
            1. **DownloadFile**
            2. 🏁 **END:** Return `$RMATemplate`
         ➔ **If [false]:**
            1. **Update **$RMATemplate**
      - Set **Name** = `'RMA_Request_' + formatDateTime([%BeginOfCurrentDay%], 'yyyyMMdd') + '.xlsx'`**
            2. **DownloadFile**
            3. 🏁 **END:** Return `$RMATemplate`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.