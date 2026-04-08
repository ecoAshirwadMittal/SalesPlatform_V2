# Microflow Detailed Specification: BDe_Column

### 📥 Inputs (Parameters)
- **$Column** (Type: ExcelImporter.Column)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$Column**
      - Set **Column_MxObjectType_Reference** = `empty`
      - Set **Column_MxObjectType** = `empty`
      - Set **Column_MxObjectMember** = `empty`
      - Set **Column_MxObjectMember_Reference** = `empty`
      - Set **Column_MxObjectReference** = `empty`
      - Set **MappingType** = `ExcelImporter.MappingType.DoNotUse`**
2. **Call Microflow **ExcelImporter.prepareReferenceHandling****
3. **Rollback**
4. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.