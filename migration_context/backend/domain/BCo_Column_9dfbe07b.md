# Microflow Analysis: BCo_Column

### Requirements (Inputs):
- **$pColumn** (A record of type: ExcelImporter.Column)

### Execution Steps:
1. **Run another process: "ExcelImporter.Column_SetDetails"**
2. **Update the **$undefined** (Object):
      - Change [ExcelImporter.Column.IsReferenceKey] to: "if( $pColumn/MappingType = ExcelImporter.MappingType.Attribute ) then 
	if( $pColumn/IsKey = ExcelImporter.YesNo.Yes ) then
		ExcelImporter.ReferenceKeyType.YesOnlyMainObject 
	else
		ExcelImporter.ReferenceKeyType.NoKey
else
	$pColumn/IsReferenceKey"**
3. **Retrieve
      - Store the result in a new variable called **$Template****
4. **Decision:** "VALID?"
   - If [ValidAttribute] -> Move to: **Find a column with the same mapping**
   - If [NoReferenceSelected] -> Move to: **WARN**
   - If [NoReferencedObjectSelected] -> Move to: **WARN**
   - If [ValidReference] -> Move to: **Finish**
   - If [NoAttributeSelected] -> Move to: **WARN**
   - If [UnUsed] -> Move to: **Set status info**
   - If [InvalidReference] -> Move to: **WARN**
   - If [InvalidAttribute] -> Move to: **WARN**
   - If [InvalidReferencedObject] -> Move to: **WARN**
   - If [(empty)] -> Move to: **WARN**
   - If [NoAssociationKeys] -> Move to: **WARN**
   - If [InvalidAutoNumberSelection] -> Move to: **WARN**
5. **Search the Database for **ExcelImporter.Column** using filter: { [ExcelImporter.Column_Template=$pColumn/ExcelImporter.Column_Template]
[MappingType='Attribute']
[ExcelImporter.Column_MxObjectMember=$pColumn/ExcelImporter.Column_MxObjectMember]
[id!=$pColumn] } (Call this list **$DuplicateMapping**)**
6. **Decision:** "found?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Show warning**
7. **Run another process: "ExcelImporter.prepareReferenceHandling"**
8. **Decision:** "Check condition"
   - If [Valid] -> Move to: **Set the column valid**
   - If [NoInputParams] -> Move to: **Show warning**
   - If [WrongNrOfInputParams] -> Move to: **Show warning**
   - If [WrongReturnType] -> Move to: **Show warning**
   - If [(empty)] -> Move to: **Finish**
9. **Update the **$undefined** (Object):
      - Change [ExcelImporter.Column.Status] to: "ExcelImporter.Status.VALID"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
