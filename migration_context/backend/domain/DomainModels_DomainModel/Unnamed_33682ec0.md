# Domain Model

## Entities

### 📦 Error
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `objectGuid` | LongAttribute | - | - | - |
| `entityName` | StringAttribute | - | - | - |
| `property` | StringAttribute | - | - | - |
| `validationFeedback` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| ValidationFeedback.User | ❌ | ✅ | ✅ | ❌ | - |

