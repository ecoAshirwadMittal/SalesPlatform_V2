# Microflow Detailed Specification: SUB_AllBidDataDownload_BatchDelete

### 📥 Inputs (Parameters)
- **$AllBidsDoc** (Type: AuctionUI.AllBidsDoc)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.AllBidDownload** Filter: `[AuctionUI.AllBidDownload_AllBidsDoc = $AllBidsDoc]` (Result: **$AllBidDownloadList_All**)**
2. **AggregateList**
3. **Create Variable **$Offset** = `0`**
4. **Create Variable **$Amount** = `@EcoATM_Direct_Sharepoint.CONST_EndOfRoundAllBidDataDownloadDeleteAmount`**
5. **Create Variable **$Processed_Count** = `0`**
6. **JavaCallAction**
7. **DB Retrieve **AuctionUI.AllBidDownload** Filter: `[AuctionUI.AllBidDownload_AllBidsDoc = $AllBidsDoc]` (Result: **$AllBidDownloadList**)**
8. **AggregateList**
9. **Delete**
10. **Update Variable **$Processed_Count** = `$Processed_Count + $RetrievedCount`**
11. **JavaCallAction**
12. **LogMessage**
13. 🔀 **DECISION:** `$Processed_Count >= $TotalItems`
   ➔ **If [true]:**
      1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update Variable **$Offset** = `$RetrievedCount + $Offset`**
         *(Merging with existing path logic)*

**Final Result:** This process concludes by returning a [Void] value.