# Domain Model

## Entities

### 📦 MxCellStyle
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 200 | - | - |
| `TextBold` | BooleanAttribute | - | false | - |
| `TextItalic` | BooleanAttribute | - | false | - |
| `TextUnderline` | BooleanAttribute | - | false | - |
| `TextAlignment` | Enum(`XLSReport.TextAlignment`) | - | - | - |
| `TextVerticalalignment` | Enum(`XLSReport.TextVerticalAlignment`) | - | - | - |
| `TextColor` | Enum(`XLSReport.MxColor`) | - | Black | - |
| `TextHeight` | IntegerAttribute | - | 10 | - |
| `BackgroundColor` | Enum(`XLSReport.MxColor`) | - | Blank | - |
| `TextRotation` | IntegerAttribute | - | 0 | - |
| `WrapText` | BooleanAttribute | - | true | - |
| `BorderTop` | IntegerAttribute | - | 0 | - |
| `BorderBottom` | IntegerAttribute | - | 0 | - |
| `BorderLeft` | IntegerAttribute | - | 0 | - |
| `BorderRight` | IntegerAttribute | - | 0 | - |
| `BorderColor` | Enum(`XLSReport.MxColor`) | - | Blank | - |
| `Format` | Enum(`XLSReport.CellFormat`) | - | General | - |
| `DecimalPlaces` | IntegerAttribute | - | 0 | - |
| `DataFormatString` | StringAttribute | 200 | - | - |
| `ThousandsSeparator` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| XLSReport.Configurator | ✅ | ✅ | ✅ | ✅ | - |

#### Validation Rules

- **TextHeight** (RangeRuleInfo): "The text height must between 1 and 20"

### 📦 MxTemplate
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `TemplateID` | AutoNumberAttribute | - | 1 | - |
| `Name` | StringAttribute | 200 | - | - |
| `Description` | StringAttribute | - | - | - |
| `DocumentType` | Enum(`XLSReport.DocumentType`) | - | XLS | - |
| `CSVSeparator` | Enum(`XLSReport.CSVSeparator`) | - | - | - |
| `DateTimePresentation` | Enum(`XLSReport.DateTimePresentation`) | - | - | - |
| `CustomeDateFormat` | StringAttribute | 200 | - | - |
| `QuotationCharacter` | StringAttribute | 1 | " | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| XLSReport.ReadOnly | ❌ | ✅ | ✅ | ❌ | - |
| XLSReport.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 MxData
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | 200 | - | - |
| `Status` | Enum(`XLSReport.Yes_no`) | - | No | - |

#### Event Handlers

- **Before Commit**: `XLSReport.BCo_MxData`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| XLSReport.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 MxSheet
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Sequence` | IntegerAttribute | - | 1 | - |
| `Name` | StringAttribute | 200 | - | - |
| `DataUsage` | BooleanAttribute | - | false | - |
| `Status` | Enum(`XLSReport.Yes_no`) | - | No | - |
| `DistinctData` | BooleanAttribute | - | false | - |
| `StartRow` | IntegerAttribute | - | 1 | - |
| `ColumnWidthDefault` | BooleanAttribute | - | true | - |
| `ColumnWidthPixels` | IntegerAttribute | - | 0 | - |
| `RowHeightDefault` | BooleanAttribute | - | true | - |
| `RowHeightPoint` | IntegerAttribute | - | 0 | - |
| `FormLayout_GroupBy` | BooleanAttribute | - | false | - |

#### Event Handlers

- **Before Commit**: `XLSReport.BCo_Sheet`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| XLSReport.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 MxSorting
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Sequence` | IntegerAttribute | - | 0 | - |
| `Summary` | StringAttribute | 200 | - | - |
| `Attribute` | StringAttribute | 200 | - | - |
| `SortingDirection` | Enum(`XLSReport.SortingDirection`) | - | Asc | - |

#### Event Handlers

- **After Create**: `XLSReport.ACr_Sorting`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| XLSReport.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 MxColumn
**Extends:** `XLSReport.MxData`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ColumnNumber` | IntegerAttribute | - | - | - |
| `ObjectAttribute` | StringAttribute | 200 | - | - |
| `DataAggregate` | BooleanAttribute | - | false | - |
| `DataAggregateFunction` | Enum(`XLSReport.AggregateFunction`) | - | - | - |
| `ResultAggregate` | BooleanAttribute | - | false | - |
| `ResultAggregateFunction` | Enum(`XLSReport.AggregateFunction`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| XLSReport.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 MxStatic
**Extends:** `XLSReport.MxData`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ColumnPlace` | IntegerAttribute | - | 0 | - |
| `RowPlace` | IntegerAttribute | - | 0 | - |
| `StaticType` | Enum(`XLSReport.StaticType`) | - | - | - |
| `AggregateFunction` | Enum(`XLSReport.AggregateFunction`) | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| XLSReport.Configurator | ✅ | ✅ | ✅ | ✅ | - |

#### Validation Rules

- **ColumnPlace** (RangeRuleInfo): "The column must be greater or equal to 0"
- **RowPlace** (RangeRuleInfo): "The row must be greater or equal to 0"

### 📦 MxColumnSettings
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ColumnIndex` | IntegerAttribute | - | 0 | - |
| `AutoSize` | BooleanAttribute | - | false | - |
| `ColumnWidth` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| XLSReport.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 MxXPath
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `RetrieveType` | Enum(`XLSReport.RetrieveAction`) | - | - | - |
| `SubVisible` | BooleanAttribute | - | false | - |

#### Event Handlers

- **Before Delete**: `XLSReport.BDe_MxXpath`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| XLSReport.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 MxRowSettings
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `RowIndex` | IntegerAttribute | - | 0 | - |
| `DefaultHeight` | BooleanAttribute | - | false | - |
| `RowHeight` | IntegerAttribute | - | 0 | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| XLSReport.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 MxConstraint
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Sequence` | IntegerAttribute | - | 0 | - |
| `Summary` | StringAttribute | 200 | - | - |
| `Attribute` | StringAttribute | 200 | - | - |
| `Constraint` | Enum(`XLSReport.ConstraintType`) | - | - | - |
| `AttributeType` | Enum(`XLSReport.AttributeType`) | - | - | - |
| `ConstraintText` | StringAttribute | 50 | - | - |
| `ConstraintNumber` | LongAttribute | - | 0 | - |
| `ConstraintDecimal` | DecimalAttribute | - | 0 | - |
| `ConstraintDateTime` | Enum(`XLSReport.DateTimeConstraint`) | - | - | - |
| `ConstraintBoolean` | BooleanAttribute | - | false | - |
| `AndOr` | Enum(`XLSReport.AndOr`) | - | AND | - |

#### Event Handlers

- **After Create**: `XLSReport.ACr_Constraint`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| XLSReport.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 MxReferenceHandling
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Reference` | StringAttribute | 200 | - | - |
| `JoinType` | Enum(`XLSReport.JOINType`) | - | INNER | - |

#### Indexes

- **Index**: `626aa053-099c-42bf-b55a-40f17b35b89c` (Ascending)

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| XLSReport.Configurator | ✅ | ✅ | ✅ | ✅ | - |
| XLSReport.ReadOnly | ❌ | ✅ | ✅ | ❌ | - |

### 📦 CustomExcel
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| XLSReport.Configurator | ❌ | ✅ | ✅ | ❌ | - |
| XLSReport.ReadOnly | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Exported_ParentXPath
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `JSONArray` | StringAttribute | - | - | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `MxCellStyle_Template` | 48d80e00-0ef8-435d-bbf3-21756b8a4c04 | 190994ac-6e9b-4fbb-8ca1-25dbb4d3b094 | Reference | Default | DeleteMeButKeepReferences |
| `MxSheet_Template` | 51c0b234-58de-40b8-aff8-77245e0d92a8 | 190994ac-6e9b-4fbb-8ca1-25dbb4d3b094 | Reference | Default | DeleteMeButKeepReferences |
| `MxData_MxSheet` | 6a8a8ea0-1578-4a74-9a3c-ed98b66ccca6 | 51c0b234-58de-40b8-aff8-77245e0d92a8 | Reference | Default | DeleteMeButKeepReferences |
| `MxData_MxCellStyle` | 6a8a8ea0-1578-4a74-9a3c-ed98b66ccca6 | 48d80e00-0ef8-435d-bbf3-21756b8a4c04 | Reference | Default | DeleteMeButKeepReferences |
| `MxSorting_MxSheet` | d3685792-3605-40bf-9471-1dc831341ded | 51c0b234-58de-40b8-aff8-77245e0d92a8 | Reference | Default | DeleteMeButKeepReferences |
| `MxStatic_MxColumn` | 5b18d890-d838-472c-a272-cfc89aaf016d | adadf6d4-e0c8-4a53-8f0c-a35558a6fa6d | Reference | Default | DeleteMeButKeepReferences |
| `ColumnSettings_MxSheet` | ef44c82f-adcf-40f0-933a-9d7a063abbf7 | 51c0b234-58de-40b8-aff8-77245e0d92a8 | Reference | Default | DeleteMeButKeepReferences |
| `ChildMxXPath_MxXPath` | 8c753819-a850-48ed-91c2-dd56c36a7009 | 8c753819-a850-48ed-91c2-dd56c36a7009 | Reference | Default | DeleteMeButKeepReferences |
| `MxXPath_MxData` | 8c753819-a850-48ed-91c2-dd56c36a7009 | 6a8a8ea0-1578-4a74-9a3c-ed98b66ccca6 | Reference | Both | DeleteMeButKeepReferences |
| `MxXPath_ParentMxXPath` | 8c753819-a850-48ed-91c2-dd56c36a7009 | 8c753819-a850-48ed-91c2-dd56c36a7009 | Reference | Default | DeleteMeButKeepReferences |
| `MxRowSettings_MxSheet` | 65c29e53-0400-4344-8bb5-4bd8897f008c | 51c0b234-58de-40b8-aff8-77245e0d92a8 | Reference | Default | DeleteMeButKeepReferences |
| `MxConstraint_MxSheet` | 0fa52ec8-87b7-4112-b642-9348afc671dc | 51c0b234-58de-40b8-aff8-77245e0d92a8 | Reference | Default | DeleteMeButKeepReferences |
| `MxConstraint_MxXPath` | 0fa52ec8-87b7-4112-b642-9348afc671dc | 8c753819-a850-48ed-91c2-dd56c36a7009 | Reference | Both | DeleteMeAndReferences |
| `MxSorting_MxXPath` | d3685792-3605-40bf-9471-1dc831341ded | 8c753819-a850-48ed-91c2-dd56c36a7009 | Reference | Both | DeleteMeAndReferences |
| `MxReferenceHandling_MxSheet` | d8204768-2ac6-4b58-bf88-12311e1164cc | 51c0b234-58de-40b8-aff8-77245e0d92a8 | Reference | Default | DeleteMeButKeepReferences |
| `MxSheet_DefaultStyle` | 51c0b234-58de-40b8-aff8-77245e0d92a8 | 48d80e00-0ef8-435d-bbf3-21756b8a4c04 | Reference | Default | DeleteMeButKeepReferences |
| `MxSheet_HeaderStyle` | 51c0b234-58de-40b8-aff8-77245e0d92a8 | 48d80e00-0ef8-435d-bbf3-21756b8a4c04 | Reference | Default | DeleteMeButKeepReferences |
| `MxTemplate_CustomExcel` | 190994ac-6e9b-4fbb-8ca1-25dbb4d3b094 | 6e4ed214-35e2-46fc-8534-3b22938d3626 | Reference | Default | DeleteMeButKeepReferences |
| `Exported_ParentXPath_MxXPath` | 4fa62309-ffdd-416c-82a3-80085899294f | 8c753819-a850-48ed-91c2-dd56c36a7009 | Reference | Default | DeleteMeButKeepReferences |
