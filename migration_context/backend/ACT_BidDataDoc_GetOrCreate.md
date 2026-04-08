# Microflow Detailed Specification: ACT_BidDataDoc_GetOrCreate

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **BidRound_BidDataDoc** via Association from **$BidRound** (Result: **$BidDataDoc**)**
2. 🔀 **DECISION:** `$BidDataDoc != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$BidDataDoc`
   ➔ **If [false]:**
      1. **Create **AuctionUI.BidDataDoc** (Result: **$NewBidDataDoc**)
      - Set **BidRound_BidDataDoc** = `$BidRound`**
      2. 🏁 **END:** Return `$NewBidDataDoc`

**Final Result:** This process concludes by returning a [Object] value.