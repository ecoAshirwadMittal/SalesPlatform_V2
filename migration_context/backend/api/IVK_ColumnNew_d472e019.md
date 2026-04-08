# Microflow Analysis: IVK_ColumnNew

### Requirements (Inputs):
- **$EnclosingContext** (A record of type: ExcelImporter.Template)

### Execution Steps:
1. **Decision:** "Has object type?"
   - If [true] -> Move to: **Create a new object**
   - If [false] -> Move to: **Send error message**
2. **Create Object
      - Store the result in a new variable called **$NewColAttributeRelation****
3. **Update the **$undefined** (Object):
      - Change [ExcelImporter.Column_MxObjectType] to: "$EnclosingContext/ExcelImporter.Template_MxObjectType"
      - Change [ExcelImporter.Column_Template] to: "$EnclosingContext"**
4. **Show Page**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
