# Domain Model

## Entities

### 📦 EcoATMSetting
### 📦 Configuration
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Filename` | StringAttribute | - | - | - |
| `IsPerEnv` | BooleanAttribute | - | false | - |
| `Microflow` | StringAttribute | - | - | - |

### 📦 Environment
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | - | - | - |
| `Comment` | StringAttribute | - | - | - |
| `Url` | StringAttribute | - | - | - |
| `IsDefault` | BooleanAttribute | - | false | - |

### 📦 ConfigurationByFile
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `FileName` | StringAttribute | 200 | - | - |
| `MD5` | StringAttribute | 200 | - | - |
| `Environment` | StringAttribute | 200 | - | - |
| `LastImportedDate` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Custom_Logging.User | ✅ | ✅ | ✅ | ✅ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `Configuration_EcoATMSetting` | ba0874c1-91ec-4008-8c9b-fcacbfe947d9 | ea9971f8-092b-4ad3-965d-e1c9b78610f5 | Reference | Default | DeleteMeButKeepReferences |
| `Environment_EcoATMSetting` | ad9de17c-ab89-4161-89b6-9a30a02133a6 | ea9971f8-092b-4ad3-965d-e1c9b78610f5 | Reference | Default | DeleteMeButKeepReferences |
