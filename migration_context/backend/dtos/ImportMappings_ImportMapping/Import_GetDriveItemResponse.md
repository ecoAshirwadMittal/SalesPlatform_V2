# Import Mapping: Import_GetDriveItemResponse

**JSON Structure:** `Sharepoint.JSON_GetDriveItemResponse`

## Mapping Structure

- **Root** (Object) → `Sharepoint.DriveItem`
  - **_id** (Value)
    - Attribute: `Sharepoint.DriveItem._id`
  - **Name** (Value)
    - Attribute: `Sharepoint.DriveItem.Name`
  - **WebUrl** (Value)
    - Attribute: `Sharepoint.DriveItem.WebUrl`
  - **Size** (Value)
    - Attribute: `Sharepoint.DriveItem.Size`
  - **ListItem** (Object) → `Sharepoint.ListItem`
    - **CreatedDateTime** (Value)
      - Attribute: `Sharepoint.ListItem.CreatedDateTime`
    - **_id** (Value)
      - Attribute: `Sharepoint.ListItem._id`
    - **LastModifiedDateTime** (Value)
      - Attribute: `Sharepoint.ListItem.LastModifiedDateTime`
    - **WebUrl** (Value)
      - Attribute: `Sharepoint.ListItem.WebUrl`
    - **ParentReference_2** (Object) → `Sharepoint.ParentReference`
      - **_id** (Value)
        - Attribute: `Sharepoint.ParentReference._id`
      - **SiteId** (Value)
        - Attribute: `Sharepoint.ParentReference.SiteId`
    - **Email** (Value)
      - Attribute: `Sharepoint.ListItem.LastModifiedByEmail`
    - **_id** (Value)
      - Attribute: `Sharepoint.ListItem.LastModifiedById`
    - **DisplayName** (Value)
      - Attribute: `Sharepoint.ListItem.LastModifiedByDisplayName`
    - **Email** (Value)
      - Attribute: `Sharepoint.ListItem.CreatedByEmail`
    - **_id** (Value)
      - Attribute: `Sharepoint.ListItem.CreatedById`
    - **DisplayName** (Value)
      - Attribute: `Sharepoint.ListItem.CreatedByDisplayName`
