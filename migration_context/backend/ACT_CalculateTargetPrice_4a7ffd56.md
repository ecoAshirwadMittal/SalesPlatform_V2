# Microflow Analysis: ACT_CalculateTargetPrice

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Run another process: "AuctionUI.ACT_ListHighBids"
      - Store the result in a new variable called **$MaxLotBidList**** ⚠️ *(This step has a safety catch if it fails)*
3. **Decision:** "Not Empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Run another process: "EcoATM_EB.SUB_RefreshEBPrice"** ⚠️ *(This step has a safety catch if it fails)*
5. **Run another process: "AuctionUI.ACT_UpdateTargetPrice"** ⚠️ *(This step has a safety catch if it fails)*
6. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
