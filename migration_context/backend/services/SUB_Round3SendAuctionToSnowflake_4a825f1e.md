# Microflow Analysis: SUB_Round3SendAuctionToSnowflake

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
2. **Decision:** "send auction data to snowflake?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Run another process: "AuctionUI.SUB_SetAuctionStatus"**
4. **Retrieve
      - Store the result in a new variable called **$Week****
5. **Run another process: "AuctionUI.SUB_SendAuctionAndSchedulingActionToSnowflake_async"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
