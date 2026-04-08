# Microflow Analysis: IVK_DuplicateTemplate

### Requirements (Inputs):
- **$Template** (A record of type: ExcelImporter.Template)

### Execution Steps:
1. **Create Object
      - Store the result in a new variable called **$NewTemplate****
2. **Update the **$undefined** (Object):
      - Change [ExcelImporter.Template.Title] to: "$Template/Title"
      - Change [ExcelImporter.Template.Description] to: "$Template/Description"
      - Change [ExcelImporter.Template.SheetIndex] to: "$Template/SheetIndex"
      - Change [ExcelImporter.Template.HeaderRowNumber] to: "$Template/HeaderRowNumber"
      - Change [ExcelImporter.Template.FirstDataRowNumber] to: "$Template/FirstDataRowNumber"
      - Change [ExcelImporter.Template.ImportAction] to: "$Template/ImportAction"
      - Change [ExcelImporter.Template_MxObjectType] to: "$Template/ExcelImporter.Template_MxObjectType"
      - Change [ExcelImporter.Template_MxObjectReference_ParentAssociation] to: "$Template/ExcelImporter.Template_MxObjectReference_ParentAssociation"
      - Change [ExcelImporter.Template.TemplateType] to: "$Template/TemplateType"**
3. **Search the Database for **ExcelImporter.Column** using filter: { [ExcelImporter.Column_Template = $Template] } (Call this list **$ColumnList**)**
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Retrieve
      - Store the result in a new variable called **$AdditionalProperties****
6. **Retrieve
      - Store the result in a new variable called **$AdditionalProperties_Copy****
7. **Update the **$undefined** (Object):
      - Change [ExcelImporter.AdditionalProperties.PrintStatisticsMessages] to: "$AdditionalProperties/PrintStatisticsMessages"
      - Change [ExcelImporter.AdditionalProperties.PrintNotFoundMessages_MainObject] to: "$AdditionalProperties/PrintNotFoundMessages_MainObject"
      - Change [ExcelImporter.AdditionalProperties.IgnoreEmptyKeys] to: "$AdditionalProperties/IgnoreEmptyKeys"
      - Change [ExcelImporter.AdditionalProperties.CommitUnchangedObjects_MainObject] to: "$AdditionalProperties/CommitUnchangedObjects_MainObject"
      - Change [ExcelImporter.AdditionalProperties.RemoveUnsyncedObjects] to: "$AdditionalProperties/RemoveUnsyncedObjects"
      - Change [ExcelImporter.AdditionalProperties.ResetEmptyAssociations] to: "$AdditionalProperties/ResetEmptyAssociations"
      - Change [ExcelImporter.AdditionalProperties_MxObjectMember_RemoveIndicator] to: "$AdditionalProperties/ExcelImporter.AdditionalProperties_MxObjectMember_RemoveIndicator"
      - **Save:** This change will be saved to the database immediately.**
8. **Search the Database for **ExcelImporter.ReferenceHandling** using filter: { [ExcelImporter.ReferenceHandling_Template = $Template] } (Call this list **$ReferenceHandlingList**)**
9. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
10. **Update the **$undefined** (Object):
      - **Save:** This change will be saved to the database immediately.**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
