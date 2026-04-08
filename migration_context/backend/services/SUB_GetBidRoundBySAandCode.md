# Microflow Detailed Specification: SUB_GetBidRoundBySAandCode

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.BidRound** Filter: `[ ( AuctionUI.BidRound_BuyerCode = $BuyerCode and AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction ) ]` (Result: **$BidRound**)**
2. 🔀 **DECISION:** `$BidRound=empty`
   ➔ **If [true]:**
      1. **Create **AuctionUI.BidRound** (Result: **$NewBidRound**)
      - Set **Submitted** = `false`
      - Set **BidRound_SchedulingAuction** = `$SchedulingAuction`
      - Set **BidRound_BuyerCode** = `$BuyerCode`**
      2. 🏁 **END:** Return `$NewBidRound`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$BidRound`

**Final Result:** This process concludes by returning a [Object] value.