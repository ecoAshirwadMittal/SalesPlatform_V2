# Import Mapping: Import_Group_List

**JSON Structure:** `MicrosoftGraph.JSON_Group_List`

## Mapping Structure

- **Root** (Object) → `MicrosoftGraph.Response`
  - **_odata_context** (Value)
    - Attribute: `MicrosoftGraph.Response._odata_context`
  - **Value** (Array) → `MicrosoftGraph.Value`
    - **JsonObject** (Object) → `MicrosoftGraph.Group`
      - **_id** (Value)
        - Attribute: `MicrosoftGraph.DirectoryObject._Id`
      - **DeletedDateTime** (Value)
        - Attribute: `MicrosoftGraph.DirectoryObject.DeletedDateTime`
      - **CreatedDateTime** (Value)
        - Attribute: `MicrosoftGraph.Group.CreatedDateTime`
      - **Description** (Value)
        - Attribute: `MicrosoftGraph.Group.Description`
      - **DisplayName** (Value)
        - Attribute: `MicrosoftGraph.Group.DisplayName`
      - **ExpirationDateTime** (Value)
        - Attribute: `MicrosoftGraph.Group.ExpirationDateTime`
      - **GroupTypes** (Array) → `MicrosoftGraph.GroupTypes`
        - **Wrapper_2** (Wrapper) → `MicrosoftGraph.NPStringArrayWrapper`
          - **Value** (Value)
            - Attribute: `MicrosoftGraph.NPStringArrayWrapper.Value`
      - **Mail** (Value)
        - Attribute: `MicrosoftGraph.Group.Mail`
      - **MailEnabled** (Value)
        - Attribute: `MicrosoftGraph.Group.MailEnabled`
      - **MailNickname** (Value)
        - Attribute: `MicrosoftGraph.Group.MailNickname`
      - **MembershipRule** (Value)
        - Attribute: `MicrosoftGraph.Group.MembershipRule`
      - **MembershipRuleProcessingState** (Value)
        - Attribute: `MicrosoftGraph.Group.MembershipRuleProcessingState`
      - **PreferredLanguage** (Value)
        - Attribute: `MicrosoftGraph.Group.PreferredLanguage`
      - **ProxyAddresses** (Array) → `MicrosoftGraph.ProxyAddresses`
        - **Wrapper_3** (Wrapper) → `MicrosoftGraph.NPStringArrayWrapper`
          - **Value** (Value)
            - Attribute: `MicrosoftGraph.NPStringArrayWrapper.Value`
      - **RenewedDateTime** (Value)
        - Attribute: `MicrosoftGraph.Group.RenewedDateTime`
      - **ResourceBehaviorOptions** (Array) → `MicrosoftGraph.ResourceBehaviorOptions`
        - **Wrapper_4** (Wrapper) → `MicrosoftGraph.NPStringArrayWrapper`
          - **Value** (Value)
            - Attribute: `MicrosoftGraph.NPStringArrayWrapper.Value`
      - **SecurityEnabled** (Value)
        - Attribute: `MicrosoftGraph.Group.SecurityEnabled`
      - **SecurityIdentifier** (Value)
        - Attribute: `MicrosoftGraph.Group.SecurityIdentifier`
      - **Theme** (Value)
        - Attribute: `MicrosoftGraph.Group.Theme`
      - **Visibility** (Value)
        - Attribute: `MicrosoftGraph.Group.Visibility`
