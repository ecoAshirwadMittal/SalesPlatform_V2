# Microflow Analysis: SUB_Purge_Auction

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Run another process: "Eco_Core.ACT_FeatureFlag_RetrieveOrCreate"
      - Store the result in a new variable called **$FeatureFlagState****
2. **Decision:** "Feature flag on?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Java Action Call
      - Store the result in a new variable called **$weekId****
5. **Run another process: "AuctionUI.SUB_Execute_CleanStep"**
6. **Run another process: "AuctionUI.SUB_Execute_CleanStep"**
7. **Run another process: "AuctionUI.SUB_Execute_CleanStep"**
8. **Run another process: "AuctionUI.SUB_Execute_CleanStep"**
9. **Run another process: "AuctionUI.SUB_Execute_CleanStep"**
10. **Run another process: "AuctionUI.SUB_Execute_CleanStep"**
11. **Run another process: "AuctionUI.SUB_Execute_CleanStep"**
12. **Run another process: "AuctionUI.SUB_Execute_CleanStep"**
13. **Run another process: "Custom_Logging.SUB_Log_Info"**
14. **Search the Database for **AuctionUI.BidRound** using filter: { [IsDeprecated] } (Call this list **$BidRoundList**)**
15. **Delete**
16. **Run another process: "Custom_Logging.SUB_Log_Info"**
17. **Search the Database for **AuctionUI.Auction** using filter: { [AuctionUI.Auction_Week=$Week] } (Call this list **$AuctionList**)**
18. **Delete**
19. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
20. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
