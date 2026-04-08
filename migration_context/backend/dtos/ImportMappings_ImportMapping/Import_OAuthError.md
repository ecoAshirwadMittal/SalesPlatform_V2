# Import Mapping: Import_OAuthError

**JSON Structure:** `MicrosoftGraph.JSON_OAuthError`

## Mapping Structure

- **Root** (Object) → `MicrosoftGraph.AuthorizationResponse`
  - **Error** (Value)
    - Attribute: `MicrosoftGraph.AuthorizationResponse.Error`
  - **Error_description** (Value)
    - Attribute: `MicrosoftGraph.AuthorizationResponse.Error_description`
  - **Error_codes** (Array) → `MicrosoftGraph.ErrorCodes`
    - **Wrapper** (Wrapper) → `MicrosoftGraph.NPStringArrayWrapper`
      - **Value** (Value)
        - Attribute: `MicrosoftGraph.NPStringArrayWrapper.Value`
  - **Timestamp** (Value)
    - Attribute: `MicrosoftGraph.AuthorizationResponse.Timestamp`
  - **Trace_id** (Value)
    - Attribute: `MicrosoftGraph.AuthorizationResponse.Trace_id`
  - **Correlation_id** (Value)
    - Attribute: `MicrosoftGraph.AuthorizationResponse.Correlation_id`
  - **Error_uri** (Value)
    - Attribute: `MicrosoftGraph.AuthorizationResponse.Error_uri`
