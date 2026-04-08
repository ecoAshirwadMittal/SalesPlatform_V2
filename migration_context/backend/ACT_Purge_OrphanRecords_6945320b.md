# Microflow Analysis: ACT_Purge_OrphanRecords

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Run another process: "AuctionUI.SUB_Execute_CleanStep"**
3. **Run another process: "AuctionUI.SUB_Execute_CleanStep"**
4. **Run another process: "Eco_Core.ACT_FeatureFlag_RetrieveOrCreate"
      - Store the result in a new variable called **$FeatureFlagState****
5. **Decision:** "Feature flag on?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Run another process: "AuctionUI.SUB_Execute_CleanStep"**
7. **Run another process: "AuctionUI.SUB_Execute_CleanStep"**
8. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
