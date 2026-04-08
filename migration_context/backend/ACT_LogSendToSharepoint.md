# Microflow Detailed Specification: ACT_LogSendToSharepoint

### 📥 Inputs (Parameters)
- **$RetryCount** (Type: Variable)
- **$UserClickBidSubmitLog** (Type: AuctionUI.BidSubmitLog)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$UserClickBidSubmitLog != empty`
   ➔ **If [true]:**
      1. **Create **AuctionUI.BidSubmitLog** (Result: **$NewBidSubmitLog**)
      - Set **SubmitDateTime** = `$UserClickBidSubmitLog/SubmitDateTime`
      - Set **RetryCount** = `$RetryCount`
      - Set **SubmitAction** = `AuctionUI.enum_SubmitBidsLogAction.Push_To_Sharepoint`
      - Set **BidSubmitLog_BidRound** = `$UserClickBidSubmitLog/AuctionUI.BidSubmitLog_BidRound`
      - Set **SubmittedBy** = `$UserClickBidSubmitLog/SubmittedBy`**
      2. 🏁 **END:** Return `$NewBidSubmitLog`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.