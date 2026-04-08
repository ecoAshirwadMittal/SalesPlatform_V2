# Import Mapping: Import_Users

**JSON Structure:** `MicrosoftGraph.JSON_Users`

## Mapping Structure

- **Root** (Object) → `MicrosoftGraph.Response`
  - **_odata_context** (Value)
    - Attribute: `MicrosoftGraph.Response._odata_context`
  - **_odata_nextLink** (Value)
    - Attribute: `MicrosoftGraph.Response._odata_nextlink`
  - **_odata_deltaLink** (Value)
    - Attribute: `MicrosoftGraph.Response._odata_deltalink`
  - **Value** (Array) → `MicrosoftGraph.Value`
    - **JsonObject** (Object) → `MicrosoftGraph.User`
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
      - **MobilePhone** (Value)
        - Attribute: `MicrosoftGraph.User.MobilePhone`
      - **OfficeLocation** (Value)
        - Attribute: `MicrosoftGraph.User.OfficeLocation`
      - **PreferredLanguage** (Value)
        - Attribute: `MicrosoftGraph.User.PreferredLanguage`
      - **Surname** (Value)
        - Attribute: `MicrosoftGraph.User.Surname`
      - **UserPrincipalName** (Value)
        - Attribute: `MicrosoftGraph.User.UserPrincipalName`
      - **_id** (Value)
        - Attribute: `MicrosoftGraph.DirectoryObject._Id`
