# Microflow Detailed Specification: ACT_RMA_DownloadTemplate

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_RMA.RMATemplate** Filter: `[IsActive]` (Result: **$LastRMATemplate**)**
2. 🔀 **DECISION:** `$LastRMATemplate!=empty`
   ➔ **If [true]:**
      1. **DownloadFile**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.