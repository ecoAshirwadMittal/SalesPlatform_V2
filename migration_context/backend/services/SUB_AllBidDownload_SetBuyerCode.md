# Microflow Detailed Specification: SUB_AllBidDownload_SetBuyerCode

### 📥 Inputs (Parameters)
- **$AllBidDownloadList** (Type: AuctionUI.AllBidDownload)
- **$BuyerCode** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. 🔄 **LOOP:** For each **$IteratorAllBidDownload** in **$AllBidDownloadList**
   │ 1. **Update **$IteratorAllBidDownload**
      - Set **BuyerCode** = `$BuyerCode`**
   └─ **End Loop**
2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.