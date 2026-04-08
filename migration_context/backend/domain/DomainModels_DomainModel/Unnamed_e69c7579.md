# Domain Model

## Entities

### 📦 ImageDimensions
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Height` | IntegerAttribute | - | 0 | - |
| `Width` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| CommunityCommons.Administrator, CommunityCommons.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 SplitItem
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Index` | IntegerAttribute | - | 0 | - |
| `Value` | StringAttribute | 2000 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| CommunityCommons.Administrator, CommunityCommons.User | ✅ | ✅ | ✅ | ✅ | - |

