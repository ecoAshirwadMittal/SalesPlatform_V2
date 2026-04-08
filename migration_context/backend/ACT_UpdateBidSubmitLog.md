# Microflow Detailed Specification: ACT_UpdateBidSubmitLog

### 📥 Inputs (Parameters)
- **$Message** (Type: Variable)
- **$Status** (Type: Variable)
- **$BidSubmitLog** (Type: AuctionUI.BidSubmitLog)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$BidSubmitLog != empty`
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Update **$BidSubmitLog** (and Save to DB)
      - Set **Message** = `$Message`
      - Set **Status** = `$Status`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.