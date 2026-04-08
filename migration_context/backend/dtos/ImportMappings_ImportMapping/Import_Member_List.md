# Import Mapping: Import_Member_List

**JSON Structure:** `MicrosoftGraph.JSON_Member_List`

## Mapping Structure

- **Root** (Object) → `MicrosoftGraph.Response`
  - **_odata_context** (Value)
    - Attribute: `MicrosoftGraph.Response._odata_context`
  - **Value** (Array) → `MicrosoftGraph.Value`
    - **JsonObject** (Object) → `MicrosoftGraph.User`
      - **_odata_type** (Value)
        - Attribute: `MicrosoftGraph.User._odata_type`
      - **_id** (Value)
        - Attribute: `MicrosoftGraph.DirectoryObject._Id`
      - **BusinessPhones** (Array) → `MicrosoftGraph.BusinessPhones`
        - **Wrapper** (Wrapper) → `MicrosoftGraph.NPStringArrayWrapper`
          - **Value** (Value)
            - Attribute: `MicrosoftGraph.NPStringArrayWrapper.Value`
      - **DisplayName** (Value)
        - Attribute: `MicrosoftGraph.User.DisplayName`
      - **GivenName** (Value)
        - Attribute: `MicrosoftGraph.User.GivenName`
      - **JobTitle** (Value)
        - Attribute: `MicrosoftGraph.User.JobTitle`
      - **Mail** (Value)
        - Attribute: `MicrosoftGraph.User.Mail`
      - **OfficeLocation** (Value)
        - Attribute: `MicrosoftGraph.User.OfficeLocation`
      - **Surname** (Value)
        - Attribute: `MicrosoftGraph.User.Surname`
      - **UserPrincipalName** (Value)
        - Attribute: `MicrosoftGraph.User.UserPrincipalName`
