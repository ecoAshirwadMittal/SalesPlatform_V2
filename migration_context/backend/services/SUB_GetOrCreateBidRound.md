# Microflow Detailed Specification: SUB_GetOrCreateBidRound

### 📥 Inputs (Parameters)
- **$BidRoundList** (Type: AuctionUI.BidRound)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$Week** (Type: EcoATM_MDM.Week)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/AuctionUI.BidRound_BuyerCode = $BuyerCode` (Result: **$ExistingBidRound**)**
2. 🔀 **DECISION:** `$ExistingBidRound = empty`
   ➔ **If [true]:**
      1. **Create **AuctionUI.BidRound** (Result: **$NewBidRound**)
      - Set **BidRound_SchedulingAuction** = `$SchedulingAuction`
      - Set **BidRound_BuyerCode** = `$BuyerCode`
      - Set **BidRound_Week** = `$Week`**
      2. **Add **$$NewBidRound** to/from list **$BidRoundList****
      3. 🏁 **END:** Return `$NewBidRound`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$ExistingBidRound`

**Final Result:** This process concludes by returning a [Object] value.