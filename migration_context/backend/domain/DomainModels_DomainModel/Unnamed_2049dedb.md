# Domain Model

## Entities

### 📦 DocumentRequest
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `RequestId` | StringAttribute | 200 | - | - |
| `Status` | Enum(`DocumentGeneration.Enum_DocumentRequest_Status`) | - | - | - |
| `FileName` | StringAttribute | 200 | - | - |
| `ResultEntity` | StringAttribute | 200 | - | - |
| `MicroflowName` | StringAttribute | 200 | - | - |
| `ContextObjectGuid` | LongAttribute | - | - | - |
| `SecurityToken` | HashedStringAttribute | - | - | - |
| `ExpirationDate` | DateTimeAttribute | - | - | - |
| `ErrorCode` | StringAttribute | 50 | - | - |
| `ErrorMessage` | StringAttribute | 500 | - | - |

#### Indexes

- **Index**: `952d647f-b174-42a9-83cb-c420e33cbfee` (Ascending)
- **Index**: `263f9123-f59a-4b0e-9cac-45a1d5902b4f` (Ascending)

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DocumentGeneration.User | ❌ | ✅ | ✅ | ❌ | `[DocumentGeneration.DocumentRequest_Session/System.Session/System.Session_User = '[%CurrentUser%]']` |

#### Validation Rules

- **RequestId** (UniqueRuleInfo): "Duplicate value for TokenId"

### 📦 Configuration
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `DeploymentType` | Enum(`DocumentGeneration.Enum_DeploymentType`) | - | MendixPublicCloud | - |
| `RegistrationStatus` | Enum(`DocumentGeneration.Enum_RegistrationStatus`) | - | Unregistered | - |
| `ApplicationUrl` | StringAttribute | 200 | - | - |
| `AccessToken` | StringAttribute | - | - | - |
| `AccessTokenExpirationDate` | DateTimeAttribute | - | - | - |
| `RefreshToken` | StringAttribute | - | - | - |
| `VerificationToken` | StringAttribute | 200 | - | - |
| `VerificationTokenExpirationDate` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DocumentGeneration.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 RegistrationWizard
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ServiceType` | Enum(`DocumentGeneration.Enum_ServiceType`) | - | - | - |
| `DeploymentType` | Enum(`DocumentGeneration.Enum_DeploymentType`) | - | MendixPublicCloud | - |
| `PersonalAccessToken` | StringAttribute | 200 | - | - |
| `AppId` | StringAttribute | 200 | - | - |
| `ApplicationUrl` | StringAttribute | 200 | - | - |
| `ManualAccessToken` | StringAttribute | - | - | - |
| `ManualRefreshToken` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DocumentGeneration.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 TokenResult
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Success` | BooleanAttribute | - | false | - |
| `Message` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DocumentGeneration.Administrator | ✅ | ✅ | ✅ | ✅ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `RegistrationWizard_Configuration` | 2bad4a78-c2b2-4637-a645-d6c4a391076c | 6dfe98c0-5aee-4205-8783-b8866caab36a | Reference | Default | DeleteMeButKeepReferences |
