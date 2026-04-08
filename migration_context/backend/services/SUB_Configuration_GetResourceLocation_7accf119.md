# Microflow Analysis: SUB_Configuration_GetResourceLocation

### Requirements (Inputs):
- **$Configuration** (A record of type: Custom_Logging.Configuration)
- **$EnvironmentName** (A record of type: Object)

### Execution Steps:
1. **Decision:** "is Per Envrionment?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
