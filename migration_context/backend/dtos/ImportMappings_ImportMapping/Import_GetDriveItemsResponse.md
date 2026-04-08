# Import Mapping: Import_GetDriveItemsResponse

**JSON Structure:** `Sharepoint.JSON_GetDriveItemsResponse`

## Mapping Structure

- **JsonObject** (Object) → `Sharepoint.DriveItem`
  - **_id** (Value)
    - Attribute: `Sharepoint.DriveItem._id`
  - **Name** (Value)
    - Attribute: `Sharepoint.DriveItem.Name`
  - **WebUrl** (Value)
    - Attribute: `Sharepoint.DriveItem.WebUrl`
  - **Size** (Value)
    - Attribute: `Sharepoint.DriveItem.Size`
  - **Folder** (Object) → `Sharepoint.Folder`
    - **ChildCount** (Value)
      - Attribute: `Sharepoint.Folder.ChildCount`
  - **ParentReference** (Object) → `Sharepoint.ParentReference`
    - **DriveId** (Value)
      - Attribute: `Sharepoint.ParentReference.DriveId`
    - **DriveType** (Value)
      - Attribute: `Sharepoint.ParentReference.DriveType`
    - **_id** (Value)
      - Attribute: `Sharepoint.ParentReference._id`
    - **Path** (Value)
      - Attribute: `Sharepoint.ParentReference.Path`
