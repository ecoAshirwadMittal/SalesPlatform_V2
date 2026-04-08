# Microflow Analysis: IVK_SaveNewTemplate

### Requirements (Inputs):
- **$Template** (A record of type: ExcelImporter.Template)

### Execution Steps:
1. **Run another process: "ExcelImporter.IVK_SaveTemplate"
      - Store the result in a new variable called **$Valid****
2. **Decision:** "valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Show Page**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
