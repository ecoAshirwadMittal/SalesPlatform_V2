# Microflow Analysis: ACT_PWSConfiguration_TestAuthentication

### Requirements (Inputs):
- **$PWSConfiguration** (A record of type: EcoATM_PWSIntegration.PWSConfiguration)

### Execution Steps:
1. **Run another process: "EcoATM_PWSIntegration.CWS_PostToken"
      - Store the result in a new variable called **$Result****
2. **Decision:** "success?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Run another process: "Custom_Logging.SUB_Log_Info"**
4. **Create Object
      - Store the result in a new variable called **$SuccessUserMessage****
5. **Show Page**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
