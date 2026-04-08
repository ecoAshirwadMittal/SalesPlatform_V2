# Microflow Analysis: ACT_BidDataDoc_PopulateExcelDoc_PreRound3

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Run another process: "EcoATM_BuyerManagement.SUB_BidRound_GetCurrentBIdRound"
      - Store the result in a new variable called **$BidRound****
3. **Decision:** "Bid Round exists?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
4. **Run another process: "Custom_Logging.SUB_Log_Error"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
