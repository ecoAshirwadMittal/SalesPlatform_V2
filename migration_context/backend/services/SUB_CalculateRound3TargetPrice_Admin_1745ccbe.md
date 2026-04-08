# Microflow Analysis: SUB_CalculateRound3TargetPrice_Admin

### Requirements (Inputs):
- **$AggInventoryHelper** (A record of type: AuctionUI.AggInventoryHelper)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Retrieve
      - Store the result in a new variable called **$Week****
3. **Retrieve
      - Store the result in a new variable called **$Auction****
4. **Decision:** "auction exists?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
5. **Show Message**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
