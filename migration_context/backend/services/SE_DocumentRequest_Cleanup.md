# Microflow Detailed Specification: SE_DocumentRequest_Cleanup

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **Create Variable **$CleanupBefore** = `addDays([%CurrentDateTime%], -@DocumentGeneration.RequestCleanupOffsetInDays)`**
3. **Create Variable **$BatchSize** = `500`**
4. **Create Variable **$TotalCount** = `0`**
5. **DB Retrieve **DocumentGeneration.DocumentRequest** Filter: `[ExpirationDate < $CleanupBefore]` (Result: **$ExpiredDocumentRequestList**)**
6. 🔀 **DECISION:** `$ExpiredDocumentRequestList != empty`
   ➔ **If [true]:**
      1. **AggregateList**
      2. **Update Variable **$TotalCount** = `$TotalCount + $BatchCount`**
      3. **Delete**
         *(Merging with existing path logic)*
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.