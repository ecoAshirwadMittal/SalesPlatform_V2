# Microflow Analysis: IVK_ColumnEdit

### Requirements (Inputs):
- **$Column** (A record of type: ExcelImporter.Column)
- **$EnclosingContext** (A record of type: ExcelImporter.Template)

### Execution Steps:
1. **Decision:** "has object type"
   - If [true] -> Move to: **Open form**
   - If [false] -> Move to: **Send error message**
2. **Show Page**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
