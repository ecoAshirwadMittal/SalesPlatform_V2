# Microflow Analysis: ACT_SendAllCaseLotsToSnowflake_Admin

### Execution Steps:
1. **Run another process: "Eco_Core.ACT_FeatureFlag_RetrieveOrCreate"
      - Store the result in a new variable called **$FeatureFlagState****
2. **Decision:** "active?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Show Message**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
