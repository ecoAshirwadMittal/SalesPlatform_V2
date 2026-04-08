# Microflow Detailed Specification: ACT_CreateBidSubmitLog

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **AuctionUI.BidSubmitLog** (Result: **$NewBidSubmitLog**)
      - Set **BidSubmitLog_BidRound** = `$BidRound`
      - Set **SubmitAction** = `AuctionUI.enum_SubmitBidsLogAction.User_Submit`
      - Set **SubmitDateTime** = `[%CurrentDateTime%]`
      - Set **SubmittedBy** = `$currentUser/Name`**
2. 🏁 **END:** Return `$NewBidSubmitLog`

**Final Result:** This process concludes by returning a [Object] value.