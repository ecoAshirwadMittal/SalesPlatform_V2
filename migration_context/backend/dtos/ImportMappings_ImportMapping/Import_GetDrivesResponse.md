# Import Mapping: Import_GetDrivesResponse

**JSON Structure:** `Sharepoint.JSON_GetDrivesResponse`

## Mapping Structure

- **JsonObject** (Object) → `Sharepoint.Drive`
  - **_id** (Value)
    - Attribute: `Sharepoint.Drive._id`
  - **DriveType** (Value)
    - Attribute: `Sharepoint.Drive.DriveType`
  - **Name** (Value)
    - Attribute: `Sharepoint.Drive.Name`
  - **Description** (Value)
    - Attribute: `Sharepoint.Drive.Description`
  - **WebUrl** (Value)
    - Attribute: `Sharepoint.Drive.WebUrl`
  - **Root_2** (Object) → `Sharepoint.DriveItem`
    - **_id** (Value)
      - Attribute: `Sharepoint.DriveItem._id`
    - **Name** (Value)
      - Attribute: `Sharepoint.DriveItem.Name`
    - **WebUrl** (Value)
      - Attribute: `Sharepoint.DriveItem.WebUrl`
    - **Size** (Value)
      - Attribute: `Sharepoint.DriveItem.Size`
