# Domain Model

## Entities

### 📦 Account
**Extends:** `System.User`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `FullName` | StringAttribute | 200 | - | - |
| `Email` | StringAttribute | 200 | - | - |
| `IsLocalUser` | BooleanAttribute | - | true | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Administration.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| Administration.User | ❌ | ✅ | ✅ | ❌ | - |
| Administration.User | ❌ | ✅ | ✅ | ❌ | `[id='[%CurrentUser%]']` |

### 📦 AccountPasswordData
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OldPassword` | StringAttribute | 200 | - | - |
| `NewPassword` | StringAttribute | 200 | - | - |
| `ConfirmPassword` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Administration.Administrator, Administration.User | ❌ | ✅ | ✅ | ❌ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `AccountPasswordData_Account` | 2306aa8a-88c5-4fbb-8121-28116c229059 | ac405dc6-f684-422c-a98c-03eb54a599aa | Reference | Default | DeleteMeButKeepReferences |
