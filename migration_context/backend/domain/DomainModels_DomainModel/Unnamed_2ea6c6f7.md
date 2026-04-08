# Domain Model

## Entities

### 📦 Template
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `TemplateName` | StringAttribute | 255 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DataImporter.Admin | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ExcelSheet
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Index` | IntegerAttribute | - | 0 | - |
| `SheetName` | StringAttribute | 31 | - | - |
| `HeaderRowStartsAt` | IntegerAttribute | - | 0 | - |
| `DataRowStartsAt` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DataImporter.Admin | ✅ | ✅ | ✅ | ✅ | - |

### 📦 ColumnAttributeMapping
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ColumnNumber` | IntegerAttribute | - | 0 | - |
| `ColumnName` | StringAttribute | - | - | - |
| `Attribute` | StringAttribute | 255 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DataImporter.Admin | ✅ | ✅ | ✅ | ✅ | - |

### 📦 CsvSheet
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Delimiter` | StringAttribute | 5 | - | - |
| `QuoteCharacter` | StringAttribute | 5 | - | - |
| `AddHeaderRow` | BooleanAttribute | - | false | - |
| `EscapeCharacter` | StringAttribute | 5 | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DataImporter.Admin | ✅ | ✅ | ✅ | ✅ | - |

### 📦 DataImporterElement
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ElementType` | IntegerAttribute | - | 0 | - |
| `PrimitiveType` | StringAttribute | 200 | - | - |
| `Path` | StringAttribute | - | - | - |
| `DecodedPath` | StringAttribute | - | - | - |
| `IsDefaultType` | BooleanAttribute | - | false | - |
| `MinOccurs` | IntegerAttribute | - | 0 | - |
| `MaxOccurs` | IntegerAttribute | - | 0 | - |
| `Nillable` | BooleanAttribute | - | false | - |
| `ExposedName` | StringAttribute | 255 | - | - |
| `ExposedItemName` | StringAttribute | 255 | - | - |
| `MaxLength` | IntegerAttribute | - | 0 | - |
| `FractionDigits` | IntegerAttribute | - | 0 | - |
| `TotalDigits` | IntegerAttribute | - | 0 | - |
| `ErrorMessage` | StringAttribute | - | - | - |
| `WarningMessage` | StringAttribute | - | - | - |
| `OriginalValue` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| DataImporter.Admin | ✅ | ✅ | ✅ | ✅ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `ColumnAttributeMapping_ExcelSheet` | a94a5126-1d89-45dc-9be0-a0428c515fa8 | 31e17cf3-469f-40aa-8852-e5c850bf3b13 | Reference | Default | DeleteMeButKeepReferences |
| `ExcelSheet_Template` | 31e17cf3-469f-40aa-8852-e5c850bf3b13 | f3714a06-33db-44a2-8ff4-141a718aabed | Reference | Default | DeleteMeButKeepReferences |
| `CsvSheet_Template` | 937fb52f-8097-4726-8470-8f97e1154bd6 | f3714a06-33db-44a2-8ff4-141a718aabed | Reference | Default | DeleteMeButKeepReferences |
| `ColumnAttributeMapping_CsvSheet` | a94a5126-1d89-45dc-9be0-a0428c515fa8 | 937fb52f-8097-4726-8470-8f97e1154bd6 | Reference | Default | DeleteMeButKeepReferences |
| `Parent` | 1706e1f9-9826-4d6c-822c-ff7e2c0f44a9 | 1706e1f9-9826-4d6c-822c-ff7e2c0f44a9 | Reference | Default | DeleteMeButKeepReferences |
| `DataImporterElement_ExcelSheet` | 1706e1f9-9826-4d6c-822c-ff7e2c0f44a9 | 31e17cf3-469f-40aa-8852-e5c850bf3b13 | Reference | Default | DeleteMeButKeepReferences |
| `DataImporterElement_CsvSheet` | 1706e1f9-9826-4d6c-822c-ff7e2c0f44a9 | 937fb52f-8097-4726-8470-8f97e1154bd6 | Reference | Default | DeleteMeButKeepReferences |
