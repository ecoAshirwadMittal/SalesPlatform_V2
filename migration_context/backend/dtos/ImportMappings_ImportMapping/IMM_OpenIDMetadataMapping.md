# Import Mapping: IMM_OpenIDMetadataMapping

**JSON Structure:** `Email_Connector.JSON_OpenIDMetadata`

## Mapping Structure

- **Root** (Object) → `Email_Connector.OAuthProvider`
  - **AuthorizationEndpoint** (Value)
    - Attribute: `Email_Connector.OAuthProvider.AuthorizationEndpoint`
  - **TokenEndpoint** (Value)
    - Attribute: `Email_Connector.OAuthProvider.TokenEndpoint`
  - **ScopeSelected** (Wrapper) → `Email_Connector.ScopeSelected`
    - **ScopeString** (Value)
      - Attribute: `Email_Connector.ScopeSelected.ScopeString`
