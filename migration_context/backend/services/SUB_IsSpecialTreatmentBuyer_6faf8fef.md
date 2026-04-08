# Microflow Analysis: SUB_IsSpecialTreatmentBuyer

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$Auction** (A record of type: AuctionUI.Auction)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
2. **Decision:** "Special Treatment?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Run another process: "Custom_Logging.SUB_Log_Info"
      - Store the result in a new variable called **$Log****
4. **Run another process: "Custom_Logging.SUB_Log_EndTimer"
      - Store the result in a new variable called **$Log****
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
