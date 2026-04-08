# Domain Model

## Entities

### 📦 Statement
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Content` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DatabaseConnector.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 Parameter
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ParameterMode` | Enum(`DatabaseConnector.ParameterMode`) | - | - | - |
| `Position` | IntegerAttribute | - | 0 | - |
| `Name` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DatabaseConnector.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ParameterString
**Extends:** `DatabaseConnector.Parameter`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Value` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DatabaseConnector.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ParameterDecimal
**Extends:** `DatabaseConnector.Parameter`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Value` | DecimalAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DatabaseConnector.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ParameterLong
**Extends:** `DatabaseConnector.Parameter`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Value` | LongAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DatabaseConnector.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ParameterDatetime
**Extends:** `DatabaseConnector.Parameter`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Value` | DateTimeAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DatabaseConnector.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ParameterObject
**Extends:** `DatabaseConnector.Parameter`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SQLTypeName` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DatabaseConnector.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ParameterList
**Extends:** `DatabaseConnector.Parameter`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SQLTypeName` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DatabaseConnector.User | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ParameterRefCursor
**Extends:** `DatabaseConnector.Parameter`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DatabaseConnector.User | ✅ | ✅ | ✅ | ✅ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `ParameterObject_Parameter` | 556bb0f4-9cda-4471-a240-7b99a79e4772 | c5b5f87d-e949-441e-b0ad-f4d9e2f21e62 | ReferenceSet | Default | DeleteMeButKeepReferences |
| `ParameterList_Parameter` | f0536f0f-2b1e-4165-ba2f-f11d505a2ada | c5b5f87d-e949-441e-b0ad-f4d9e2f21e62 | ReferenceSet | Default | DeleteMeButKeepReferences |
| `ParameterRefCursor_Parameter` | fea0dad4-b5e9-4b83-a163-4ff5872d687d | c5b5f87d-e949-441e-b0ad-f4d9e2f21e62 | ReferenceSet | Default | DeleteMeButKeepReferences |
| `Statement_Parameter` | 17237647-dc4b-444b-a891-0c159dd9eb12 | c5b5f87d-e949-441e-b0ad-f4d9e2f21e62 | ReferenceSet | Default | DeleteMeButKeepReferences |
