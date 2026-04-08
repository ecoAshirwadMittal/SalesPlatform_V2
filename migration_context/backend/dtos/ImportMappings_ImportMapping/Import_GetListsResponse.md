# Import Mapping: Import_GetListsResponse

**JSON Structure:** `Sharepoint.JSON_GetListsResponse`

## Mapping Structure

- **JsonObject** (Object) → `Sharepoint.List`
  - **_id** (Value)
    - Attribute: `Sharepoint.List._id`
  - **Name** (Value)
    - Attribute: `Sharepoint.List.Name`
  - **CreatedDateTime** (Value)
    - Attribute: `Sharepoint.List.CreatedDateTime`
  - **LastModifiedDateTime** (Value)
    - Attribute: `Sharepoint.List.LastModifiedDateTime`
  - **Drive** (Object) → `Sharepoint.Drive`
    - **Description** (Value)
      - Attribute: `Sharepoint.Drive.Description`
    - **_id** (Value)
      - Attribute: `Sharepoint.Drive._id`
    - **Name** (Value)
      - Attribute: `Sharepoint.Drive.Name`
    - **DriveType** (Value)
      - Attribute: `Sharepoint.Drive.DriveType`
