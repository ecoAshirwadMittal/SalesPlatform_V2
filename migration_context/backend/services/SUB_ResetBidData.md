# Microflow Detailed Specification: SUB_ResetBidData

### 📥 Inputs (Parameters)
- **$BidDataList** (Type: AuctionUI.BidData)

### ⚙️ Execution Flow (Logic Steps)
1. 🔄 **LOOP:** For each **$IteratorBidData** in **$BidDataList**
   │ 1. **Update **$IteratorBidData**
      - Set **IsChanged** = `false`
      - Set **BidAmount** = `$IteratorBidData/TempDABidAmount`**
   └─ **End Loop**
2. **Commit/Save **$BidDataList** to Database**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.