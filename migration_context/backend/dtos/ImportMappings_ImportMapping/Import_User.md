# Import Mapping: Import_User

**JSON Structure:** `MicrosoftGraph.JSON_User`

## Mapping Structure

- **Root** (Object) → `MicrosoftGraph.User`
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
