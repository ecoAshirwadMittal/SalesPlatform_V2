# Microflow Analysis: IVK_CancelTemplate

### Requirements (Inputs):
- **$Template** (A record of type: ExcelImporter.Template)

### Execution Steps:
1. **Decision:** "template was new?"
   - If [false] -> Move to: **Rollback the changes in the template**
   - If [true] -> Move to: **Rollback the changes in the template**
2. **Rollback**
3. **Run another process: "ExcelImporter.ValidateTemplate"
      - Store the result in a new variable called **$valid****
4. **Close Form**
5. **Decision:** "valid?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Finish**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
