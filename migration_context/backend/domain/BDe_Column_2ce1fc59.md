# Microflow Analysis: BDe_Column

### Requirements (Inputs):
- **$Column** (A record of type: ExcelImporter.Column)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [ExcelImporter.Column_MxObjectType_Reference] to: "empty"
      - Change [ExcelImporter.Column_MxObjectType] to: "empty"
      - Change [ExcelImporter.Column_MxObjectMember] to: "empty"
      - Change [ExcelImporter.Column_MxObjectMember_Reference] to: "empty"
      - Change [ExcelImporter.Column_MxObjectReference] to: "empty"
      - Change [ExcelImporter.Column.MappingType] to: "ExcelImporter.MappingType.DoNotUse"**
2. **Run another process: "ExcelImporter.prepareReferenceHandling"**
3. **Rollback**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
