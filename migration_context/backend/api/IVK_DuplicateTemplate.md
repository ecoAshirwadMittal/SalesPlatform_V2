# Microflow Detailed Specification: IVK_DuplicateTemplate

### 📥 Inputs (Parameters)
- **$Template** (Type: ExcelImporter.Template)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **ExcelImporter.Template** (Result: **$NewTemplate**)**
2. **Update **$NewTemplate**
      - Set **Title** = `$Template/Title`
      - Set **Description** = `$Template/Description`
      - Set **SheetIndex** = `$Template/SheetIndex`
      - Set **HeaderRowNumber** = `$Template/HeaderRowNumber`
      - Set **FirstDataRowNumber** = `$Template/FirstDataRowNumber`
      - Set **ImportAction** = `$Template/ImportAction`
      - Set **Template_MxObjectType** = `$Template/ExcelImporter.Template_MxObjectType`
      - Set **Template_MxObjectReference_ParentAssociation** = `$Template/ExcelImporter.Template_MxObjectReference_ParentAssociation`
      - Set **TemplateType** = `$Template/TemplateType`**
3. **DB Retrieve **ExcelImporter.Column** Filter: `[ExcelImporter.Column_Template = $Template]` (Result: **$ColumnList**)**
4. 🔄 **LOOP:** For each **$Column** in **$ColumnList**
   │ 1. **Create **ExcelImporter.Column** (Result: **$NewColumn**)**
   │ 2. **Update **$NewColumn** (and Save to DB)
      - Set **ColNumber** = `$Column/ColNumber`
      - Set **Text** = `$Column/Text`
      - Set **MappingType** = `$Column/MappingType`
      - Set **IsKey** = `$Column/IsKey`
      - Set **IsReferenceKey** = `$Column/IsReferenceKey`
      - Set **Status** = `$Column/Status`
      - Set **Details** = `$Column/Details`
      - Set **CaseSensitive** = `$Column/CaseSensitive`
      - Set **Column_Template** = `$NewTemplate`
      - Set **Column_MxObjectType_Reference** = `$Column/ExcelImporter.Column_MxObjectType_Reference`
      - Set **Column_MxObjectType** = `$Column/ExcelImporter.Column_MxObjectType`
      - Set **Column_MxObjectMember** = `$Column/ExcelImporter.Column_MxObjectMember`
      - Set **Column_MxObjectMember_Reference** = `$Column/ExcelImporter.Column_MxObjectMember_Reference`
      - Set **Column_MxObjectReference** = `$Column/ExcelImporter.Column_MxObjectReference`
      - Set **Column_Microflows** = `$Column/ExcelImporter.Column_Microflows`
      - Set **Column_ValueType** = `$Column/ExcelImporter.Column_ValueType`
      - Set **FindAttribute** = `$Column/FindAttribute`
      - Set **FindReference** = `$Column/FindReference`
      - Set **FindObjectType** = `$Column/FindObjectType`
      - Set **FindMicroflow** = `$Column/FindMicroflow`
      - Set **DataSource** = `$Column/DataSource`**
   └─ **End Loop**
5. **Retrieve related **Template_AdditionalProperties** via Association from **$Template** (Result: **$AdditionalProperties**)**
6. **Retrieve related **Template_AdditionalProperties** via Association from **$Template** (Result: **$AdditionalProperties_Copy**)**
7. **Update **$AdditionalProperties_Copy** (and Save to DB)
      - Set **PrintStatisticsMessages** = `$AdditionalProperties/PrintStatisticsMessages`
      - Set **PrintNotFoundMessages_MainObject** = `$AdditionalProperties/PrintNotFoundMessages_MainObject`
      - Set **IgnoreEmptyKeys** = `$AdditionalProperties/IgnoreEmptyKeys`
      - Set **CommitUnchangedObjects_MainObject** = `$AdditionalProperties/CommitUnchangedObjects_MainObject`
      - Set **RemoveUnsyncedObjects** = `$AdditionalProperties/RemoveUnsyncedObjects`
      - Set **ResetEmptyAssociations** = `$AdditionalProperties/ResetEmptyAssociations`
      - Set **AdditionalProperties_MxObjectMember_RemoveIndicator** = `$AdditionalProperties/ExcelImporter.AdditionalProperties_MxObjectMember_RemoveIndicator`**
8. **DB Retrieve **ExcelImporter.ReferenceHandling** Filter: `[ExcelImporter.ReferenceHandling_Template = $Template]` (Result: **$ReferenceHandlingList**)**
9. 🔄 **LOOP:** For each **$ReferenceHandling** in **$ReferenceHandlingList**
   │ 1. **DB Retrieve **ExcelImporter.ReferenceHandling** Filter: `[ExcelImporter.ReferenceHandling_Template = $NewTemplate] [ExcelImporter.ReferenceHandling_MxObjectReference=$ReferenceHandling/ExcelImporter.ReferenceHandling_MxObjectReference]` (Result: **$ReferenceHandling_copy**)**
   │ 2. **Update **$ReferenceHandling_copy** (and Save to DB)
      - Set **Handling** = `$ReferenceHandling/Handling`
      - Set **DataHandling** = `$ReferenceHandling/DataHandling`
      - Set **PrintNotFoundMessages** = `$ReferenceHandling/PrintNotFoundMessages`
      - Set **CommitUnchangedObjects** = `$ReferenceHandling/CommitUnchangedObjects`**
   └─ **End Loop**
10. **Update **$NewTemplate** (and Save to DB)**
11. 🏁 **END:** Return `$NewTemplate`

**Final Result:** This process concludes by returning a [Object] value.