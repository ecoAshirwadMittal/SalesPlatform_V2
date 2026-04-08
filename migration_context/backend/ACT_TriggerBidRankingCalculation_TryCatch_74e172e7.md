# Microflow Analysis: ACT_TriggerBidRankingCalculation_TryCatch

### Requirements (Inputs):
- **$ResetRanking** (A record of type: Object)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "AuctionUI.ACT_TriggerBidRankingCalculation"
      - Store the result in a new variable called **$Response**** ⚠️ *(This step has a safety catch if it fails)*
2. **Decision:** "success?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
