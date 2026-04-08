# Microflow Analysis: SUB_Authentication_SetDefaultConfiguration

### Requirements (Inputs):
- **$Authentication** (A record of type: MicrosoftGraph.Authentication)

### Execution Steps:
1. **Search the Database for **MicrosoftGraph.StringArrayWrapper** using filter: { [MicrosoftGraph.StringArrayWrapper_StringArray/MicrosoftGraph.StringArray/MicrosoftGraph.Response_types_supported = $Authentication]
[Value = 'code'] } (Call this list **$StringArrayWrapper_ResponseType**)**
2. **Search the Database for **MicrosoftGraph.StringArrayWrapper** using filter: { [MicrosoftGraph.StringArrayWrapper_StringArray/MicrosoftGraph.StringArray/MicrosoftGraph.Response_modes_supported = $Authentication]
[Value = 'query'] } (Call this list **$StringArrayWrapper_ResponseMode**)**
3. **Search the Database for **MicrosoftGraph.StringArrayWrapper** using filter: { [MicrosoftGraph.StringArrayWrapper_StringArray/MicrosoftGraph.StringArray/MicrosoftGraph.Scopes = $Authentication]
[Value = 'email'
or
Value = 'offline_access'
or
Value = 'openid'
or
Value = 'profile'
] } (Call this list **$StringArrayWrapperList_Scopes**)**
4. **Update the **$undefined** (Object):
      - Change [MicrosoftGraph.SelectedResponseType] to: "$StringArrayWrapper_ResponseType"
      - Change [MicrosoftGraph.SelectedResponseMode] to: "$StringArrayWrapper_ResponseMode
"
      - Change [MicrosoftGraph.SelectedScopes] to: "$StringArrayWrapperList_Scopes
"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
