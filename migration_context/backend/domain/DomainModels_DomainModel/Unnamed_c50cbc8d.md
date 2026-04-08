# Domain Model

> Store locks

## Entities

### 📦 Lock
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ObjectID` | LongAttribute | - | - | - |
| `ObjectType` | StringAttribute | 200 | - | - |
| `ObjectSource` | StringAttribute | 200 | - | - |
| `ObjectName` | StringAttribute | 200 | - | - |
| `LockedBy` | StringAttribute | 200 | - | - |
| `LockedOn` | DateTimeAttribute | - | - | - |
| `LockUpdatedOn` | DateTimeAttribute | - | - | - |
| `Active` | BooleanAttribute | - | false | - |

#### Indexes

- **Index**: `96e1cdaa-a699-4c5e-8559-301ddeb079a0` (Ascending)

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_Lock.Admin | ✅ | ✅ | ✅ | ✅ | - |
| EcoATM_Lock.User | ❌ | ✅ | ✅ | ❌ | - |

### 📦 ObjectInfo
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `LockedBy` | StringAttribute | 200 | - | - |
| `IsCurrentUserAllowed` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| EcoATM_Lock.Admin, EcoATM_Lock.User | ✅ | ✅ | ✅ | ✅ | - |

