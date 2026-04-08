# Microflow Analysis: IVK_Template_ConnectMatchingAttributes

### Requirements (Inputs):
- **$Template** (A record of type: ExcelImporter.Template)

### Execution Steps:
1. **Decision:** "Check condition"
   - If [false] -> Move to: **Send error message**
   - If [true] -> Move to: **Activity**
2. **Show Message**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
