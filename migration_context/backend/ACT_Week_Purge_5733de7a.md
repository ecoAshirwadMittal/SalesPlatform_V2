# Microflow Analysis: ACT_Week_Purge

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Run another process: "Eco_Core.ACT_FeatureFlag_RetrieveOrCreate"
      - Store the result in a new variable called **$FeatureFlagState****
2. **Decision:** "Feature flag on?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Run another process: "Custom_Logging.SUB_Log_Warning"**
4. **Show Message**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
