# Microflow Detailed Specification: CWS_PostToken

### 📥 Inputs (Parameters)
- **$PWSConfiguration** (Type: EcoATM_PWSIntegration.PWSConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **RestCall**
3. **ImportXml**
4. **Call Microflow **Custom_Logging.SUB_Log_Info****
5. 🏁 **END:** Return `$TokenResponse/access_token`

**Final Result:** This process concludes by returning a [String] value.