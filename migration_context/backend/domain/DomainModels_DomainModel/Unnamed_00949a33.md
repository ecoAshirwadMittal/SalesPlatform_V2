# Domain Model

> Changes: 11-05-2010 All: - Added an AdditionalProperties object so the user is able to configure what is logged when the excel file is being imported - Added ReferenceHandlingEnum option Create all associated objects Modeler: - Improved the validation of the template Java - Minor fix for validation in the GetHeader function - Bug fix for setting references to created referenced objects - Bug fix for using dates as key for associated objects - Bug fix so dates can be imported correctly - Fix for importing references ( added sorting for all key maps and sets, better use of refernce sets) 03-05-2010 All: - Removed the dependency between the template and template document. 28-04-2010 Java: - Bug fix for using a microflow as value parser - Bug fix for converting values to the correct types - Bug fix for syncrhonizing more than 2000 rows - Improved all logging messages 12-04-2010 All: - Converted the application to 2.5, removed all (compile) errors and implemented all new 2.5 features to make the module even better 03-02-2010 Modeler - Fixed the template validation for the startup validation and the validation after saving the template. 02-02-2010 Modeler - Removed all template document reference selectors and changed it to a Dataview All xls documents should be uploaded for a specific templates so there couldn't be any unwanted side affects. In de future all TemplateDocument forms will be removed Modeler - Added custom save/cancel buttons and moved the validation to these two buttons. TODO, the validation of the Template still needs some improvements with better and more info for the user Java - Moved some custom code from the excel importer implementation

## Entities

### 📦 Column
> The Column contains the specific mapping for each usefull column. All the mapping and all settings for each column will be configured here.

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ColNumber` | IntegerAttribute | - | - | - |
| `Text` | StringAttribute | - | - | - |
| `MappingType` | Enum(`ExcelImporter.MappingType`) | - | DoNotUse | - |
| `IsKey` | Enum(`ExcelImporter.YesNo`) | - | No | - |
| `IsReferenceKey` | Enum(`ExcelImporter.ReferenceKeyType`) | - | NoKey | - |
| `Status` | Enum(`ExcelImporter.Status`) | - | INFO | - |
| `Details` | StringAttribute | 1000 | - | - |
| `CaseSensitive` | Enum(`ExcelImporter.YesNo`) | - | No | - |
| `FindAttribute` | StringAttribute | 200 | - | - |
| `FindReference` | StringAttribute | 200 | - | - |
| `FindObjectType` | StringAttribute | 200 | - | - |
| `FindMicroflow` | StringAttribute | 200 | - | - |
| `DataSource` | Enum(`ExcelImporter.DataSource`) | - | CellValue | - |
| `AttributeTypeEnum` | Enum(`MxModelReflection.PrimitiveTypes`) | - | - | - |
| `InputMask` | StringAttribute | 20 | - | - |

#### Event Handlers

- **Before Commit**: `ExcelImporter.BCo_Column`
- **Before Delete**: `ExcelImporter.BDe_Column`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| ExcelImporter.Readonly | ❌ | ✅ | ✅ | ❌ | - |
| ExcelImporter.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 Template
> The template is the starting entity for the excel module. This wrapper contains all the settings for the excel import. Within this template several columns can be added.

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Nr` | AutoNumberAttribute | - | 1 | - |
| `Title` | StringAttribute | 50 | - | - |
| `Description` | StringAttribute | - | - | - |
| `SheetIndex` | IntegerAttribute | - | 1 | - |
| `HeaderRowNumber` | IntegerAttribute | - | 1 | - |
| `FirstDataRowNumber` | IntegerAttribute | - | 2 | - |
| `Status` | Enum(`ExcelImporter.Status`) | - | INFO | - |
| `ImportAction` | Enum(`ExcelImporter.ImportActions`) | - | SynchronizeObjects | - |
| `TemplateType` | Enum(`ExcelImporter.TemplateType`) | - | Normal | - |

#### Event Handlers

- **After Create**: `ExcelImporter.ACr_Template`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| ExcelImporter.Readonly | ❌ | ✅ | ✅ | ❌ | - |
| ExcelImporter.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 TemplateDocument
> The TemplateDocument acts as a file which can be used for two purposes, 1. generate a template, 2. Import the excel sheet using the associated template

**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| ExcelImporter.Configurator | ✅ | ✅ | ✅ | ✅ | - |
| ExcelImporter.Readonly | ❌ | ✅ | ✅ | ❌ | - |

### 📦 ReferenceHandling
> References For each reference that is used in the mapping, you can configure what should happen if a referenced object is not found. Just like in the modeler the value of a reference(set) can be added to the current values of be overwritten. Print message when reference is not found Keep track of all the object keys in this association that could not be found. Warning: This consumes a lot of memory since all values need to be remembered. Commit unchanged objects Even if there aren't any changes to the object still commit the objects in order to execute the events.

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Handling` | Enum(`ExcelImporter.ReferenceHandlingEnum`) | - | FindCreate | - |
| `DataHandling` | Enum(`ExcelImporter.ReferenceDataHandling`) | - | Overwrite | - |
| `PrintNotFoundMessages` | BooleanAttribute | - | false | - |
| `CommitUnchangedObjects` | BooleanAttribute | - | true | - |
| `IgnoreEmptyKeys` | BooleanAttribute | - | true | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| ExcelImporter.Configurator | ✅ | ✅ | ✅ | ✅ | - |
| ExcelImporter.Readonly | ❌ | ✅ | ✅ | ❌ | - |

### 📦 AdditionalProperties
> Besides the basic settings in the Template this entity contains all advanced settings. This entity can not exist without a template

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `PrintStatisticsMessages` | Enum(`ExcelImporter.StatisticsLevel`) | - | AllStatistics | - |
| `PrintNotFoundMessages_MainObject` | BooleanAttribute | - | true | - |
| `IgnoreEmptyKeys` | BooleanAttribute | - | true | - |
| `CommitUnchangedObjects_MainObject` | BooleanAttribute | - | true | - |
| `RemoveUnsyncedObjects` | Enum(`ExcelImporter.RemoveIndicator`) | - | Nothing | - |
| `ResetEmptyAssociations` | BooleanAttribute | - | false | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| ExcelImporter.Configurator | ✅ | ✅ | ✅ | ✅ | - |
| ExcelImporter.Readonly | ❌ | ✅ | ✅ | ❌ | - |

### 📦 XMLDocumentTemplate
**Extends:** `System.FileDocument`

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| ExcelImporter.Configurator | ✅ | ✅ | ✅ | ✅ | - |

### 📦 Log
#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Logline` | StringAttribute | - | - | - |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| ExcelImporter.Configurator | ✅ | ✅ | ✅ | ✅ | - |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `Column_Template` | 54055879-30ba-4e2a-a021-98d828aa264c | 586fe7f4-5f49-484c-b25b-a27b28f4c512 | Reference | Default | DeleteMeButKeepReferences |
| `ReferenceHandling_Template` | cf085b87-1e89-48f3-af2c-5998b78882f7 | 586fe7f4-5f49-484c-b25b-a27b28f4c512 | Reference | Default | DeleteMeButKeepReferences |
| `TemplateDocument_Template` | 680d3f96-0e78-4f70-9842-1e9a8da09be0 | 586fe7f4-5f49-484c-b25b-a27b28f4c512 | Reference | Default | DeleteMeButKeepReferences |
| `Template_AdditionalProperties` | 586fe7f4-5f49-484c-b25b-a27b28f4c512 | 715cfd78-fc27-4d2c-9db4-eb8e129cd9a0 | Reference | Both | DeleteMeAndReferences |
| `Column_MasterColumn` | 54055879-30ba-4e2a-a021-98d828aa264c | 54055879-30ba-4e2a-a021-98d828aa264c | Reference | Default | DeleteMeButKeepReferences |
| `Template_MasterTemplate` | 586fe7f4-5f49-484c-b25b-a27b28f4c512 | 586fe7f4-5f49-484c-b25b-a27b28f4c512 | Reference | Default | DeleteMeButKeepReferences |
| `XMLDocumentTemplate_Template` | 92e2f95b-a307-48a9-99fa-48fb4a724891 | 586fe7f4-5f49-484c-b25b-a27b28f4c512 | Reference | Default | DeleteMeButKeepReferences |
| `Log_XMLDocumentTemplate` | 92cdb90a-1403-4e5c-94dc-862a62008265 | 92e2f95b-a307-48a9-99fa-48fb4a724891 | Reference | Default | DeleteMeButKeepReferences |
