# Microflow Analysis: SUB_SendAuctionAndSchedulingActionToSnowflake_async

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Export Xml** ⚠️ *(This step has a safety catch if it fails)*
2. **Create Variable**
3. **Execute Database Query
      - Store the result in a new variable called **$AUCTIONS_UPSERT_AUCTION_AND_SCHEDULE**** ⚠️ *(This step has a safety catch if it fails)*
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
