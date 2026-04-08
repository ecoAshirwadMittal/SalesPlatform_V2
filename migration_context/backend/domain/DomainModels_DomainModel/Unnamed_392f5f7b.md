# Domain Model

## Entities

### 📦 Geolocation
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Timestamp` | DateTimeAttribute | - | - | - |
| `Latitude` | StringAttribute | 200 | - | - |
| `Longitude` | StringAttribute | 200 | - | - |
| `Altitude` | StringAttribute | 200 | - | - |
| `Accuracy` | StringAttribute | 200 | - | - |
| `AltitudeAccuracy` | StringAttribute | 200 | - | - |
| `Heading` | StringAttribute | 200 | - | - |
| `Speed` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| NanoflowCommons.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 Position
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Latitude` | StringAttribute | 200 | - | - |
| `Longitude` | StringAttribute | 200 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| NanoflowCommons.User | ✅ | ✅ | ✅ | ✅ | - |

