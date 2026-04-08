# Microflow Analysis: NAV_DynamicMenu_Pricing

### Execution Steps:
1. **Run another process: "Eco_Core.ACT_FeatureFlag_RetrieveOrCreate"
      - Store the result in a new variable called **$FeatureFlagState****
2. **Decision:** "Feature Allowed?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Show Page**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
