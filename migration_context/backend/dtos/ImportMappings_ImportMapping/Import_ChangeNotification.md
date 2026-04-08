# Import Mapping: Import_ChangeNotification

**JSON Structure:** `MicrosoftGraph.JSON_ChangeNotification`

## Mapping Structure

- **Root** (Object) → `MicrosoftGraph.Response`
  - **ValidationTokens** (Array) → `MicrosoftGraph.ValidationTokens`
    - **Wrapper** (Wrapper) → `MicrosoftGraph.NPStringArrayWrapper`
      - **Value** (Value)
        - Attribute: `MicrosoftGraph.NPStringArrayWrapper.Value`
  - **Value** (Array) → `MicrosoftGraph.Value`
    - **JsonObject** (Object) → `MicrosoftGraph.ChangeNotification`
      - **SubscriptionId** (Value)
        - Attribute: `MicrosoftGraph.ChangeNotification.SubscriptionId`
      - **SubscriptionExpirationDateTime** (Value)
        - Attribute: `MicrosoftGraph.ChangeNotification.SubscriptionExpirationDateTime`
      - **ChangeType** (Value)
        - Attribute: `MicrosoftGraph.ChangeNotification.ChangeType`
      - **Resource** (Value)
        - Attribute: `MicrosoftGraph.ChangeNotification.Resource`
      - **ResourceData** (Object) → `MicrosoftGraph.ResourceData`
        - **_odata_type** (Value)
          - Attribute: `MicrosoftGraph.ResourceData._odata_type`
        - **_odata_id** (Value)
          - Attribute: `MicrosoftGraph.ResourceData._odata_id`
        - **_odata_etag** (Value)
          - Attribute: `MicrosoftGraph.ResourceData._odata_etag`
        - **_id** (Value)
          - Attribute: `MicrosoftGraph.ResourceData._Id`
      - **ClientState** (Value)
        - Attribute: `MicrosoftGraph.ChangeNotification.ClientState`
      - **TenantId** (Value)
        - Attribute: `MicrosoftGraph.ChangeNotification.TenantId`
