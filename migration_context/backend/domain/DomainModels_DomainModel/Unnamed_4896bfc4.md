# Domain Model

## Entities

### 📦 Site
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `_id` | StringAttribute | - | - | - |
| `Name` | StringAttribute | 1024 | - | - |
| `DisplayName` | StringAttribute | 1024 | - | - |
| `Description` | StringAttribute | - | - | - |
| `CreatedDateTime` | DateTimeAttribute | - | - | - |
| `LastModifiedDateTime` | DateTimeAttribute | - | - | - |
| `WebUrl` | StringAttribute | 4096 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Sharepoint.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 SearchSitesResult
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `DUmmy` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Sharepoint.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 OpenSiteByIdRequest
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `_id` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Sharepoint.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 Explorer
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Dummy` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Sharepoint.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 List
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `_id` | StringAttribute | - | - | - |
| `Name` | StringAttribute | - | - | - |
| `CreatedDateTime` | DateTimeAttribute | - | - | - |
| `LastModifiedDateTime` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Sharepoint.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 ListItem
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `DisplayName` | StringAttribute | 1024 | - | - |
| `CreatedDateTime` | DateTimeAttribute | - | - | - |
| `ETag` | StringAttribute | 1024 | - | - |
| `_id` | StringAttribute | 1024 | - | - |
| `LastModifiedDateTime` | DateTimeAttribute | - | - | - |
| `WebUrl` | StringAttribute | 4096 | - | - |
| `CreatedByDisplayName` | StringAttribute | 1024 | - | - |
| `CreatedByEmail` | StringAttribute | 1024 | - | - |
| `CreatedById` | StringAttribute | 1024 | - | - |
| `LastModifiedByEmail` | StringAttribute | 1024 | - | - |
| `LastModifiedById` | StringAttribute | 1024 | - | - |
| `LastModifiedByDisplayName` | StringAttribute | 1024 | - | - |
| `ContentTypeDisplayName` | StringAttribute | 1024 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Sharepoint.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Column
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 1024 | - | - |
| `ColumnType` | Enum(`Sharepoint.ColumnType`) | - | - | - |
| `DisplayValue` | StringAttribute | - | - | - |
| `StringValue` | StringAttribute | - | - | - |
| `NumericValue` | LongAttribute | - | 0 | - |
| `DateTimeValue` | DateTimeAttribute | - | - | - |
| `BooleanValue` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Sharepoint.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 DriveItemContents
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Sharepoint.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 DriveItem
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `_id` | StringAttribute | 200 | - | - |
| `Name` | StringAttribute | 1024 | - | - |
| `Size` | LongAttribute | - | 0 | - |
| `Description` | StringAttribute | - | - | - |
| `WebUrl` | StringAttribute | 2048 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Sharepoint.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 ContentType
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `_Id` | StringAttribute | 200 | - | - |
| `Name` | StringAttribute | 1024 | - | - |
| `Description` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Sharepoint.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Drive
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `_id` | StringAttribute | 200 | - | - |
| `Name` | StringAttribute | 200 | - | - |
| `DriveType` | StringAttribute | 200 | - | - |
| `Path` | StringAttribute | 2048 | - | - |
| `Description` | StringAttribute | - | - | - |
| `WebUrl` | StringAttribute | 2048 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Sharepoint.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Folder
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ChildCount` | LongAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Sharepoint.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 File
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `MimeType` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Sharepoint.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Children
#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Sharepoint.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 CreateUploadSessionRequest
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ConflictBehavior` | StringAttribute | 200 | - | - |
| `Description` | StringAttribute | - | - | - |
| `FileSize` | IntegerAttribute | - | 0 | - |
| `Name` | StringAttribute | 2048 | - | - |

### 📦 CreateUploadSessionResponse
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `UploadUrl` | StringAttribute | - | - | - |
| `ExpirationDateTime` | DateTimeAttribute | - | - | - |

### 📦 CreateDriveItemHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `DriveId` | StringAttribute | 200 | - | - |
| `SiteId` | StringAttribute | 200 | - | - |
| `ParentId` | StringAttribute | 200 | - | - |
| `ListId` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Sharepoint.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 CompletedFileResponse
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `_id` | StringAttribute | - | - | - |
| `Name` | StringAttribute | - | - | - |
| `Size` | IntegerAttribute | - | 0 | - |

### 📦 ParentReference
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `_id` | StringAttribute | 200 | - | - |
| `SiteId` | StringAttribute | 2048 | - | - |
| `DriveId` | StringAttribute | 200 | - | - |
| `DriveType` | StringAttribute | 200 | - | - |
| `Path` | StringAttribute | 2048 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Sharepoint.Administrator | ❌ | ✅ | ✅ | ❌ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `Site_SearchSitesResult` | ac3d0ce3-d118-44a8-a73b-7b204970a1ca | d1358d41-c23a-4bc4-a181-b33eddca0c26 | Reference | Default | DeleteMeButKeepReferences |
| `OpenSiteByIdRequest_Explorer` | 073a62fa-4045-4a59-8522-19b4ab236faf | 92a691a3-7fa1-4f81-bfe9-4c56e0b7d5cd | Reference | Both | DeleteMeButKeepReferences |
| `SelectedSite` | 92a691a3-7fa1-4f81-bfe9-4c56e0b7d5cd | ac3d0ce3-d118-44a8-a73b-7b204970a1ca | Reference | Both | DeleteMeAndReferences |
| `Explorer_SearchSitesResult` | 92a691a3-7fa1-4f81-bfe9-4c56e0b7d5cd | d1358d41-c23a-4bc4-a181-b33eddca0c26 | Reference | Both | DeleteMeButKeepReferences |
| `ListItem_Explorer` | 314ce2fd-3a6e-475b-a965-d03a5718ce2d | 92a691a3-7fa1-4f81-bfe9-4c56e0b7d5cd | Reference | Default | DeleteMeButKeepReferences |
| `List_Explorer` | 70a9eec2-42b7-475a-8ba3-ebf06c7ba1db | 92a691a3-7fa1-4f81-bfe9-4c56e0b7d5cd | Reference | Default | DeleteMeButKeepReferences |
| `Fields` | 2011b6a5-67cf-44ba-8609-8205abbfc2c6 | 314ce2fd-3a6e-475b-a965-d03a5718ce2d | Reference | Default | DeleteMeButKeepReferences |
| `SelectedList` | 92a691a3-7fa1-4f81-bfe9-4c56e0b7d5cd | 70a9eec2-42b7-475a-8ba3-ebf06c7ba1db | Reference | Both | DeleteMeAndReferences |
| `DriveItem_ListItem` | 61cf5640-aed7-4e13-8b1a-adf3b0d70c3e | 314ce2fd-3a6e-475b-a965-d03a5718ce2d | Reference | Both | DeleteMeButKeepReferences |
| `ListItemContentType` | 21c441e7-0128-4c99-a04e-1afd908afbd7 | 314ce2fd-3a6e-475b-a965-d03a5718ce2d | Reference | Both | DeleteMeButKeepReferences |
| `DriveItem_DriveItemContents` | 61cf5640-aed7-4e13-8b1a-adf3b0d70c3e | 42ee2cfa-f4df-453e-9ef2-45c0c6a55c7d | Reference | Default | DeleteMeButKeepReferences |
| `List_Drive` | 70a9eec2-42b7-475a-8ba3-ebf06c7ba1db | 5555d933-0965-41fd-a518-734cf3ebe17f | Reference | Both | DeleteMeButKeepReferences |
| `SelectedDrive` | 5555d933-0965-41fd-a518-734cf3ebe17f | 92a691a3-7fa1-4f81-bfe9-4c56e0b7d5cd | Reference | Both | DeleteMeButKeepReferences |
| `Root` | 5555d933-0965-41fd-a518-734cf3ebe17f | 61cf5640-aed7-4e13-8b1a-adf3b0d70c3e | Reference | Both | DeleteMeButKeepReferences |
| `FolderMeta` | 8db0242c-4eb3-4ee0-88d8-4adc54ca6841 | 61cf5640-aed7-4e13-8b1a-adf3b0d70c3e | Reference | Both | DeleteMeButKeepReferences |
| `FileMeta` | 0b7c3568-1516-45a5-b42d-ef10740f7024 | 61cf5640-aed7-4e13-8b1a-adf3b0d70c3e | Reference | Both | DeleteMeButKeepReferences |
| `ListedDriveItems` | 61cf5640-aed7-4e13-8b1a-adf3b0d70c3e | 92a691a3-7fa1-4f81-bfe9-4c56e0b7d5cd | Reference | Default | DeleteMeButKeepReferences |
| `Parent` | e0e9717d-a8d8-4516-9704-cadee0d05126 | 61cf5640-aed7-4e13-8b1a-adf3b0d70c3e | Reference | Both | DeleteMeButKeepReferences |
| `Child` | 61cf5640-aed7-4e13-8b1a-adf3b0d70c3e | e0e9717d-a8d8-4516-9704-cadee0d05126 | Reference | Default | DeleteMeButKeepReferences |
| `CreateDriveItemHelper_DriveItem` | 831ec07d-1ea4-4a1d-bc9a-df36956fb875 | 61cf5640-aed7-4e13-8b1a-adf3b0d70c3e | Reference | Default | DeleteMeButKeepReferences |
| `CreateDriveItemHelper_Explorer` | 831ec07d-1ea4-4a1d-bc9a-df36956fb875 | 92a691a3-7fa1-4f81-bfe9-4c56e0b7d5cd | Reference | Default | DeleteMeButKeepReferences |
| `ParentReference_ListItem` | 273b5bbd-2167-48c1-af7b-e89d9d1cc388 | 314ce2fd-3a6e-475b-a965-d03a5718ce2d | Reference | Both | DeleteMeButKeepReferences |
| `DriveItem_ParentReference` | 61cf5640-aed7-4e13-8b1a-adf3b0d70c3e | 273b5bbd-2167-48c1-af7b-e89d9d1cc388 | Reference | Both | DeleteMeButKeepReferences |
