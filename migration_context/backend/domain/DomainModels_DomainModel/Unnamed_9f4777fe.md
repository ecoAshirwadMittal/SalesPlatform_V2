# Domain Model

## Entities

### 📦 Query
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `OQL` | StringAttribute | - | - | - |
| `CSV` | StringAttribute | - | - | - |
| `ShowAs` | Enum(`OQL.ShowAs`) | - | TABLE | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| OQL.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 CSVDownload
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| OQL.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 ExamplePerson
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Number` | AutoNumberAttribute | - | 1 | - |
| `Name` | StringAttribute | 200 | - | - |
| `DateOfBirth` | DateTimeAttribute | - | - | - |
| `Age` | IntegerAttribute | - | 0 | - |
| `LongAge` | LongAttribute | - | 0 | - |
| `Active` | BooleanAttribute | - | false | - |
| `HeightInDecimal` | DecimalAttribute | - | 0 | - |
| `Gender` | Enum(`OQL.Gender`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| OQL.Administrator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ExamplePersonResult
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Number` | LongAttribute | - | 0 | - |
| `Name` | StringAttribute | 200 | - | - |
| `DateOfBirth` | DateTimeAttribute | - | - | - |
| `Age` | IntegerAttribute | - | 0 | - |
| `LongAge` | LongAttribute | - | 0 | - |
| `Active` | BooleanAttribute | - | false | - |
| `HeightInDecimal` | DecimalAttribute | - | 0 | - |
| `Gender` | Enum(`OQL.Gender`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| OQL.Administrator | ❌ | ✅ | ✅ | ❌ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `ExamplePersonResult_ExamplePerson` | 5ca173be-66d9-4fa3-abed-fd06ce7f92a6 | 33544bf8-4c3b-4305-85ae-ffe1cd49a2d0 | Reference | Default | DeleteMeButKeepReferences |
| `Result_MarriedTo` | 5ca173be-66d9-4fa3-abed-fd06ce7f92a6 | 33544bf8-4c3b-4305-85ae-ffe1cd49a2d0 | Reference | Default | DeleteMeButKeepReferences |
| `MarriedTo` | 33544bf8-4c3b-4305-85ae-ffe1cd49a2d0 | 33544bf8-4c3b-4305-85ae-ffe1cd49a2d0 | Reference | Default | DeleteMeButKeepReferences |
