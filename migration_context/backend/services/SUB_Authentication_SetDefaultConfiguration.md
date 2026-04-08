# Microflow Detailed Specification: SUB_Authentication_SetDefaultConfiguration

### 📥 Inputs (Parameters)
- **$Authentication** (Type: MicrosoftGraph.Authentication)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **MicrosoftGraph.StringArrayWrapper** Filter: `[MicrosoftGraph.StringArrayWrapper_StringArray/MicrosoftGraph.StringArray/MicrosoftGraph.Response_types_supported = $Authentication] [Value = 'code']` (Result: **$StringArrayWrapper_ResponseType**)**
2. **DB Retrieve **MicrosoftGraph.StringArrayWrapper** Filter: `[MicrosoftGraph.StringArrayWrapper_StringArray/MicrosoftGraph.StringArray/MicrosoftGraph.Response_modes_supported = $Authentication] [Value = 'query']` (Result: **$StringArrayWrapper_ResponseMode**)**
3. **DB Retrieve **MicrosoftGraph.StringArrayWrapper** Filter: `[MicrosoftGraph.StringArrayWrapper_StringArray/MicrosoftGraph.StringArray/MicrosoftGraph.Scopes = $Authentication] [Value = 'email' or Value = 'offline_access' or Value = 'openid' or Value = 'profile' ]` (Result: **$StringArrayWrapperList_Scopes**)**
4. **Update **$Authentication**
      - Set **SelectedResponseType** = `$StringArrayWrapper_ResponseType`
      - Set **SelectedResponseMode** = `$StringArrayWrapper_ResponseMode`
      - Set **SelectedScopes** = `$StringArrayWrapperList_Scopes`**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.