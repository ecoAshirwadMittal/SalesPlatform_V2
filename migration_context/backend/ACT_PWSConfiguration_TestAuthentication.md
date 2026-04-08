# Microflow Detailed Specification: ACT_PWSConfiguration_TestAuthentication

### 📥 Inputs (Parameters)
- **$PWSConfiguration** (Type: EcoATM_PWSIntegration.PWSConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_PWSIntegration.CWS_PostToken** (Result: **$Result**)**
2. 🔀 **DECISION:** `$Result!=empty`
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. **Create **EcoATM_PWS.UserMessage** (Result: **$SuccessUserMessage**)
      - Set **Title** = `'Successfully authenticated'`
      - Set **Message** = `'Parameters are validated'`
      - Set **CSSClass** = `'pws-file-upload-success'`
      - Set **IsSuccess** = `true`**
      3. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Error****
      2. **Create **EcoATM_PWS.UserMessage** (Result: **$NewUserMessage**)
      - Set **Title** = `'Unable to authenticate'`
      - Set **Message** = `'Please, check the logs for more details.'`
      - Set **CSSClass** = `'pws-file-upload-error'`
      - Set **IsSuccess** = `false`**
      3. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
      4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.