# Microflow Analysis: SUB_SendCaseLotsToSnowflakeInitiate

### Execution Steps:
1. **Search the Database for **EcoATM_PWSMDM.CaseLot** using filter: { Show everything } (Call this list **$CaseLotList**)**
2. **Run another process: "Eco_Core.ACT_FeatureFlag_RetrieveOrCreate"
      - Store the result in a new variable called **$FeatureFlagState****
3. **Decision:** "active?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
