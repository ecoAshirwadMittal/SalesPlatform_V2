# Microflow Detailed Specification: SUB_BidDownloadHelper_Associate_BidDataDoc

### 📥 Inputs (Parameters)
- **$NewBidDataDoc** (Type: AuctionUI.BidDataDoc)
- **$BidDownload_HelperList** (Type: AuctionUI.BidDownload_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. 🔄 **LOOP:** For each **$IteratorBidDownload_Helper** in **$BidDownload_HelperList**
   │ 1. **Update **$IteratorBidDownload_Helper**
      - Set **BidDownload_Helper_BidDataDoc** = `$NewBidDataDoc`**
   └─ **End Loop**
3. **LogMessage**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.