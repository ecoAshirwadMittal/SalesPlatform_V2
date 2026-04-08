# Import Mapping: Import_BatchResponse

**JSON Structure:** `MicrosoftGraph.JSON_BatchResponse`

## Mapping Structure

- **Root** (Object) → `MicrosoftGraph.Batch`
  - **Responses** (Array) → `MicrosoftGraph.Responses`
    - **JsonObject** (Object) → `MicrosoftGraph.BatchResponse`
      - **_id** (Value)
        - Attribute: `MicrosoftGraph.BatchResponse._id`
      - **Status** (Value)
        - Attribute: `MicrosoftGraph.BatchResponse.Status`
      - **Headers** (Object) → `MicrosoftGraph.Headers`
        - **Location** (Value)
          - Attribute: `MicrosoftGraph.Headers.Location`
