# Microflow Detailed Specification: SUB_Authentication_GetUserAccessURL

### 📥 Inputs (Parameters)
- **$Authentication** (Type: MicrosoftGraph.Authentication)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **MicrosoftGraph.SUB_Authorization_GetOrCreate** (Result: **$Authorization**)**
2. **JavaCallAction**
3. **Call Microflow **MicrosoftGraph.SUB_Scopes_Get** (Result: **$Scope**)**
4. **Call Microflow **MicrosoftGraph.SUB_Authorization_SetNonce** (Result: **$Nonce**)**
5. **Create Variable **$Location** = `$Authentication/Authorization_Endpoint+'?' +'client_id='+$Authentication/AppId +'&response_type='+urlEncode($Authentication/MicrosoftGraph.SelectedResponseType/MicrosoftGraph.StringArrayWrapper/Value) +'&redirect_uri='+$URL+urlEncode('/microsoftgraph/oauth/v2/callback_azure') +'&prompt='+getKey($Authentication/Prompt) +'&response_mode='+$Authentication/MicrosoftGraph.SelectedResponseMode/MicrosoftGraph.StringArrayWrapper/Value +'&scope='+$Scope +'&state='+$Authorization/State +$Nonce`**
6. **Commit/Save **$Authorization** to Database**
7. 🏁 **END:** Return `$Location`

**Final Result:** This process concludes by returning a [String] value.