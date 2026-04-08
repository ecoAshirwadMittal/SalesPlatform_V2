# Export Mapping: Export_BatchRequest

**JSON Structure:** `MicrosoftGraph.JSON_BatchRequest`

## Mapping Structure

- **Root** (Object) → `MicrosoftGraph.Batch`
  - **Requests** (Array) → `MicrosoftGraph.Requests`
    - **JsonObject** (Object) → `MicrosoftGraph.BatchRequest`
      - **_id** (Value)
        - Attribute: `MicrosoftGraph.BatchRequest._id`
      - **DependsOn** (Array) → `MicrosoftGraph.DependsOn`
        - **Wrapper** (Wrapper) → `MicrosoftGraph.NPStringArrayWrapper`
          - **Value** (Value)
            - Attribute: `MicrosoftGraph.NPStringArrayWrapper.Value`
      - **Method** (Value)
        - Attribute: `MicrosoftGraph.BatchRequest.Method`
      - **Url** (Value)
        - Attribute: `MicrosoftGraph.BatchRequest.Url`
      - **Headers** (Object) → `MicrosoftGraph.Headers`
        - **ConsistencyLevel** (Value)
          - Attribute: `MicrosoftGraph.Headers.ConsistencyLevel`
        - **Content_Type** (Value)
          - Attribute: `MicrosoftGraph.Headers.Content_Type`
      - **Body** (Object) → `MicrosoftGraph.Body`
