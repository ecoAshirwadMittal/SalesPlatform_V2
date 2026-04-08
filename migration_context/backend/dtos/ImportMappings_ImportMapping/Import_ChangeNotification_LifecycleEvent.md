# Import Mapping: Import_ChangeNotification_LifecycleEvent

**JSON Structure:** `MicrosoftGraph.JSON_ChangeNotification_LifecycleEvent`

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
      - **TenantId** (Value)
        - Attribute: `MicrosoftGraph.ChangeNotification.TenantId`
      - **ClientState** (Value)
        - Attribute: `MicrosoftGraph.ChangeNotification.ClientState`
      - **LifecycleEvent** (Value)
        - Attribute: `MicrosoftGraph.ChangeNotification.LifecycleEvent`
