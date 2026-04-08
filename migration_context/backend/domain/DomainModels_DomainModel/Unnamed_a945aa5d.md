# Domain Model

## Entities

### 📦 IdleTimeout
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `LastRecordedActivity` | DateTimeAttribute | - | - | - |
| `IdleTimeExtension` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_Direct_Theme.Administrator | ✅ | ✅ | ✅ | ✅ | `[System.owner='[%CurrentUser%]']` |
| EcoATM_Direct_Theme.User | ✅ | ✅ | ✅ | ✅ | `[System.owner='[%CurrentUser%]']` |

### 📦 UiHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `DateTime` | DateTimeAttribute | - | - | - |
| `String` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_Direct_Theme.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_Direct_Theme.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 TimerHelper
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Minutes` | IntegerAttribute | - | 0 | - |
| `Seconds` | IntegerAttribute | - | 0 | - |
| `MultiTabCheckInterval` | IntegerAttribute | - | 10 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_Direct_Theme.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_Direct_Theme.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 IdleTimeoutConfiguration
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SerialNumber` | AutoNumberAttribute | - | 1 | - |
| `IdleTimeoutWarning` | IntegerAttribute | - | 300 | - |
| `MultiTabCheckInterval` | IntegerAttribute | - | 10 | - |
| `TimerWarningMessage` | StringAttribute | - | You have been idle for quite long now. You will be logged out soon. | - |
| `WarningSeconds` | IntegerAttribute | - | 59 | - |
| `IsActive` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_Direct_Theme.Administrator | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_Direct_Theme.User | ❌ | ✅ | ✅ | ❌ | - |

