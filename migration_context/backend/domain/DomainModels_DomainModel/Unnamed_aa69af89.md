# Domain Model

## Entities

### 📦 Tile
**Extends:** `System.Image`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `TileName` | StringAttribute | 200 | - | - |
| `URL` | StringAttribute | 200 | - | - |
| `LinkType` | Enum(`Eco_Core.LinkType`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Eco_Core.Admin | ✅ | ✅ | ✅ | ✅ | - |
| Eco_Core.User | ✅ | ✅ | ✅ | ❌ | - |

### 📦 PWSFeatureFlag
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 200 | - | - |
| `Active` | BooleanAttribute | - | false | - |
| `Description` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| Eco_Core.Admin | ✅ | ✅ | ✅ | ✅ | - |
| Eco_Core.User | ✅ | ✅ | ✅ | ❌ | - |

