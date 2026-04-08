# Import Mapping: Import_Error

**JSON Structure:** `MicrosoftGraph.JSON_Error`

## Mapping Structure

- **Root** (Object) → `MicrosoftGraph.Response`
  - **Error** (Object) → `MicrosoftGraph.Error`
    - **Code** (Value)
      - Attribute: `MicrosoftGraph.Error.Code`
    - **Message** (Value)
      - Attribute: `MicrosoftGraph.Error.Message`
    - **InnerError** (Object) → `MicrosoftGraph.InnerError`
      - **Date** (Value)
        - Attribute: `MicrosoftGraph.InnerError.Date`
      - **Request_id** (Value)
        - Attribute: `MicrosoftGraph.InnerError.Request_id`
      - **Client_request_id** (Value)
        - Attribute: `MicrosoftGraph.InnerError.Client_request_id`
