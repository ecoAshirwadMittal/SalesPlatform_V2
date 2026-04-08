# Microflow Analysis: CWS_PostCreateRMA

### Requirements (Inputs):
- **$JSONContent** (A record of type: Object)
- **$BearerToken** (A record of type: Object)
- **$PWSConfiguration** (A record of type: EcoATM_PWSIntegration.PWSConfiguration)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Rest Call** ⚠️ *(This step has a safety catch if it fails)*
5. **Run another process: "EcoATM_PWSIntegration.ACT_AuditRestAPICalls"**
6. **Import Xml** ⚠️ *(This step has a safety catch if it fails)*
7. **Update the **$undefined** (Object):
      - Change [EcoATM_PWSIntegration.OracleResponse.HTTPCode] to: "$latestHttpResponse/StatusCode"
      - Change [EcoATM_PWSIntegration.OracleResponse.JSONResponse] to: "$CreateOrderHttpResponse"**
8. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
