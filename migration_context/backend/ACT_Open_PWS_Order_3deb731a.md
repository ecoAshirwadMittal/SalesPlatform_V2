# Microflow Analysis: ACT_Open_PWS_Order

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Run another process: "Eco_Core.ACT_FeatureFlag_RetrieveOrCreate"
      - Store the result in a new variable called **$FeatureFlagState****
5. **Decision:** "Feature Allowed?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **BuyerCode Available?**
6. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
7. **Show Message**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
