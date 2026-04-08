# Import Mapping: Import_GetListItemsResponse

**JSON Structure:** `Sharepoint.JSON_GetListItemsResponse`

## Mapping Structure

- **JsonObject** (Object) → `Sharepoint.ListItem`
  - **CreatedDateTime** (Value)
    - Attribute: `Sharepoint.ListItem.CreatedDateTime`
  - **ETag** (Value)
    - Attribute: `Sharepoint.ListItem.ETag`
  - **_id** (Value)
    - Attribute: `Sharepoint.ListItem._id`
  - **LastModifiedDateTime** (Value)
    - Attribute: `Sharepoint.ListItem.LastModifiedDateTime`
  - **WebUrl** (Value)
    - Attribute: `Sharepoint.ListItem.WebUrl`
  - **ParentReference** (Object) → `Sharepoint.ParentReference`
    - **_id** (Value)
      - Attribute: `Sharepoint.ParentReference._id`
    - **SiteId** (Value)
      - Attribute: `Sharepoint.ParentReference.SiteId`
  - **ContentType** (Object) → `Sharepoint.ContentType`
    - **_id** (Value)
      - Attribute: `Sharepoint.ContentType._Id`
    - **Name** (Value)
      - Attribute: `Sharepoint.ContentType.Name`
  - **DriveItem** (Object) → `Sharepoint.DriveItem`
    - **Description** (Value)
      - Attribute: `Sharepoint.DriveItem.Description`
    - **_id** (Value)
      - Attribute: `Sharepoint.DriveItem._id`
    - **Name** (Value)
      - Attribute: `Sharepoint.DriveItem.Name`
    - **WebUrl** (Value)
      - Attribute: `Sharepoint.DriveItem.WebUrl`
    - **Size** (Value)
      - Attribute: `Sharepoint.DriveItem.Size`
    - **File** (Object) → `Sharepoint.File`
      - **MimeType** (Value)
        - Attribute: `Sharepoint.File.MimeType`
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
