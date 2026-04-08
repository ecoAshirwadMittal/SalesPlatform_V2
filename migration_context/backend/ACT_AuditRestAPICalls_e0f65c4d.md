# Microflow Analysis: ACT_AuditRestAPICalls

### Requirements (Inputs):
- **$HttpResponse** (A record of type: System.HttpResponse)
- **$Request** (A record of type: Object)
- **$Response** (A record of type: Object)
- **$ENUM_Method** (A record of type: Object)
- **$Success** (A record of type: Object)
- **$URL** (A record of type: Object)
- **$Error** (A record of type: System.Error)

### Execution Steps:
1. **Run another process: "Eco_Core.ACT_FeatureFlag_RetrieveOrCreate"
      - Store the result in a new variable called **$FeatureFlagState****
2. **Decision:** "FeatureFlagState?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Create Object
      - Store the result in a new variable called **$NewIntegration****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
