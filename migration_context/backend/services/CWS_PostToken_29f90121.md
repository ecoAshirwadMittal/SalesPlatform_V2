# Microflow Analysis: CWS_PostToken

### Requirements (Inputs):
- **$PWSConfiguration** (A record of type: EcoATM_PWSIntegration.PWSConfiguration)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Rest Call** ⚠️ *(This step has a safety catch if it fails)*
3. **Import Xml** ⚠️ *(This step has a safety catch if it fails)*
4. **Run another process: "Custom_Logging.SUB_Log_Info"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
